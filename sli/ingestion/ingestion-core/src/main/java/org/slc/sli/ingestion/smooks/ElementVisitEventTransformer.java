/**
 *
 */
package org.slc.sli.ingestion.smooks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;

import org.apache.commons.io.IOUtils;
import org.milyn.delivery.ContentHandlerConfigMap;
import org.milyn.delivery.VisitSequence;
import org.milyn.event.ElementProcessingEvent;

/**
 * org.milyn.event.types.ElementVisitEvent replacing transformer.
 * The original implementation of the ElementVisitEvent ctor generates report eagerly, that leads to huge performance penalty.
 *
 * @author okrook
 *
 */
public class ElementVisitEventTransformer implements ClassFileTransformer {

    /* (non-Javadoc)
     * @see java.lang.instrument.ClassFileTransformer#transform(java.lang.ClassLoader, java.lang.String, java.lang.Class, java.security.ProtectionDomain, byte[])
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain domain, byte[] bytes)
            throws IllegalClassFormatException {

        if ("org/milyn/event/types/ElementVisitEvent".equals(className)) {
            return hijectElementVisitEvent(loader, bytes);
        }

        return bytes;
    }

    private static byte[] hijectElementVisitEvent(ClassLoader loader, byte[] bytes) {
        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = null;

        preloadClasses(pool, loader, ContentHandlerConfigMap.class, VisitSequence.class, ElementProcessingEvent.class);

        ByteArrayInputStream input = null;
        try {
            input = new ByteArrayInputStream(bytes);

            clazz = loadClass(pool, input);

            for (CtConstructor ctor : clazz.getDeclaredConstructors()) {
                if (ctor.callsSuper()) {
                    ctor.setBody("{ super($1); $0.configMapping = $2; $0.sequence = $3; }");
                }
            }

            return clazz.toBytecode();
        } catch (IOException e) {
            return bytes;
        } catch (CannotCompileException e) {
            return bytes;
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    private static void preloadClasses(ClassPool pool, ClassLoader loader, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            loadClass(pool, loader, clazz);
        }
    }

    private static CtClass loadClass(ClassPool pool, ClassLoader loader, Class<?> classToLoad) {
        InputStream input = null;
        try {
            input = loader.getResourceAsStream(classToLoad.getName().replace('.', '/') + ".class");

            return loadClass(pool, input);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    private static CtClass loadClass(ClassPool pool, InputStream input) {
        try {
            return pool.makeClass(input);
        } catch (IOException e) {
            return null;
        }
    }
}
