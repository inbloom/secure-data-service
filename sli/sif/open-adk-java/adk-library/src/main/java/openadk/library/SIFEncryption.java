//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;

import openadk.library.infra.Password;
import openadk.library.infra.PasswordAlgorithm;

import java.security.spec.AlgorithmParameterSpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Provides easy read and write access to encrypted passwords in the 
 * Authetication(US) or Identity(UK) objects
 * <p>
 * This class is abstract. Concrete instances can be obtained by calling the 
 * appropriate <code>getInstance</code> overload. 
 * </p>
 *  <p>
 * The SIFEncryption class uses the following properties to determine its default 
 * algorithm and key for writing and for finding keys for decrypting passwords. 
 * Please see <see cref="AgentProperties"/> for details
 * </p>
 * <table border="1" cellpadding="2" cellspacing="3">
 *      <tr>
 *          <td><center><b>Property</b></center></td>
 *          <td><b>Description</b></td>
 *      </tr>
 *      <tr>
 *          <td><b><code>adk.encryption.algorithm</code></b></td>
 *          <td>The default algorithm used for writing passwords</td>
 *      </tr>
 *      <tr>
 *          <td><b><code>adk.encryption.key</code></b></td>
 *          <td>The name of the default key to use for encryption</td>
 *      </tr>
 *      <tr>
 *          <td><b><code>adk.encryption.keys.[keyname]</code></b></td>
 *          <td>The actual key to use for encryption or decryption where "keyname"
 * 			matches the @KeyName attribute of the AuthenticationInfoPassword object</td>
 *      </tr>
 * </table>
 * <p>
 * For example usage, please see the AuthenticationProvider and 
 * AuthenticationSubscriber example projects.
 * </p>
 * @author Andrew Elmhorst
 * @version ADK 1.5.1
 */
public abstract class SIFEncryption {
	
    /**
     * The algorithm that this class was initialized with
     */
    private PasswordAlgorithm fAlgorithm;    
	/**
	 * The key name that this class was initialized with. This matches the
	 * @KeyName attribute of the <code>AuthenticationInfoPassword</code> class
	 */
	private String fkeyName;
	/**
	 * The current instance of the class that is in use. The instance
	 * may be reused between objects, as long as the algorithm and KeyName
	 * remain the same.
	 */
	private static SIFEncryption sCurrentInstance;
	
	/**
	 * Used for encoding binary data to BASE64
	 */
	protected BASE64Encoder fEncoder = new BASE64Encoder();
	/**
	 * Used for decoding binary data from BASE64
	 */
	protected BASE64Decoder fDecoder = new BASE64Decoder();

	/**
	 * Instances of this class can only be created by calls to the getInstance() methods
	 * @param algorithm The encryption or hashing algorithm to use
	 * @param keyName The name of the key being used
	 */
	protected SIFEncryption( PasswordAlgorithm algorithm, String keyName )
	{
		fkeyName = keyName;
		fAlgorithm = algorithm;
	}
	
	/**
	 * Creates an instance of SIFEncryption that uses the specified PasswordAlgorithm, 
	 * keyName and key.
	 * 
	 * @param algorithm The algorithm to use for encrypting or decrypting passwords
	 * @param keyName The name of the encryption key to use.
	 * @param key The encryption key to use. This parameter is ignored for SHA1 
	 * and MD5 because they are not keyed hash algorithms. It's also ignore for BASE64
	 * @return an instance of the SIFEncryption class that can read and write passwords using
	 * the chosen algorithm and key.
	 * @throws NoSuchAlgorithmException Thrown if the specified algorithm is not available. For example, 
	 * RC2 and RSA are not available in the default java 1.4 cipher suites.
	 * @throws NoSuchPaddingException Thrown if the padding method (which is always PKCS5Padding) is not available
	 */
	public static synchronized SIFEncryption getInstance(
		PasswordAlgorithm algorithm,
		String keyName,
		byte[] key
		) throws NoSuchAlgorithmException, NoSuchPaddingException
	{
		if ( sCurrentInstance != null )
		{
			if( sCurrentInstance.getAlgorithm().equals( algorithm ) &&
			  ( sCurrentInstance.getKeyName().equals( keyName ) || sCurrentInstance.getKey() == null ) )
			{
				return sCurrentInstance;
			}
			else
			{
				sCurrentInstance = null;
			}
		}
		
		// Base64 is not supported in all SIF locales, and thus is not looked
		// up using the PasswordAlgorithm enum
		if ( algorithm.valueEquals( "base64" ) )
		{
			sCurrentInstance = new SIFClearTextEncryption( algorithm, keyName );
		}
		else if ( algorithm.equals( PasswordAlgorithm.SHA1 ))
		{
	        sCurrentInstance = new SIFHashEncryption( algorithm, keyName, MessageDigest.getInstance( "SHA1" ) );
		}
		else if ( algorithm.equals( PasswordAlgorithm.MD5 ))
		{
	        sCurrentInstance = new SIFHashEncryption( algorithm, keyName, MessageDigest.getInstance( "MD5" ) );
		}
		else if ( algorithm.equals(PasswordAlgorithm.DES ))
		{
			sCurrentInstance = new SIFSymmetricEncryption( algorithm, keyName, "DES", key );
		}
		else if ( algorithm.equals(PasswordAlgorithm.TRIPLEDES ))
		{
			sCurrentInstance = new SIFSymmetricEncryption( algorithm, keyName, "DESede", key );
		}
		else if ( algorithm.equals( PasswordAlgorithm.RC2 ))
		{
			sCurrentInstance = new SIFSymmetricEncryption( algorithm, keyName, "RC2", key );
		}
		else
		{
			throw new ADKNotSupportedException( "Encryption algorithm " + algorithm + " is not supported." );
		}
		return sCurrentInstance;
	}


	
	/**
	 * Creates an instance of SIFEncryption that can decrypt 
	 * the password field automatically, using settings 
	 * defined in the agent's properties.
	 * <p>
	 * This method searches the agent properties in effect 
	 * for the zone and looks for one that matches the key
	 * defined in the AuthenticationInfoPassword object.
	 * If it finds one, it returns an instance of SIFEncryption
	 * that has been initialized with the proper key and
	 * encryption algorithm for the field.
	 * <p>
	 * This method looks for a property named
	 * <code>adk.encryption.keys.[keyName]</code> where <code>[keyName]</code> is the name
	 * of the key field defined by the AuthenticationInfoPassword
	 * field.
	 * @param password The password object that needs to be decrypted
	 * @param zone The zone that is in scope for the current message
	 * @return An instance of SIFEncryption that can read the password from the given A
	 * uthenticationInfoPassword object
	 * @throws IOException If the key stored in the agent properties cannot be converted 
	 * from BASE64 to binary
	 * @throws NoSuchAlgorithmException Thrown if the specified algorithm is not available. 
	 * For example, RC2 and RSA are not available in the default java 1.4 cipher suites.
	 * @throws NoSuchPaddingException Thrown if the padding method 
	 * (which is always PKCS5Padding) is not available
	 */
	public static SIFEncryption getInstance(
		Password password,	Zone zone ) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException
	{
		if ( sCurrentInstance != null &&
		     sCurrentInstance.getAlgorithm().equals( password.getAlgorithm() ) &&
		  	( sCurrentInstance.getKeyName().equals( password.getKeyName() ) || sCurrentInstance.getKey() == null ) )
		{
			return sCurrentInstance;	
		}
		byte[] key = zone.getProperties().getEncryptionKey( password.getKeyName() );
		return getInstance( PasswordAlgorithm.wrap( password.getAlgorithm() ), password.getKeyName(), key );
	}


	/**
	 * Creates an instance of SIFEncryption that can be used
	 * for writing the AuthenticationInfoPassword field,
	 * using settings from the agent's properties
	 * <p>
	 * This method searches for two properties in the agent
	 * properties. <code>adk.encryption.algorithm</code> returns the default
	 * algorithm the agent uses for encryption.
	 * <code>adk.encryption.key</code> returns the name of the key to use for
	 * encryption, which, if required, will be read from the
	 * <code>adk.encryption.keys.[keyName]</code> property. The
	 * <code>adk.encryption.keys.[keyName]</code> property is not used or required
	 * for base64, SHA1, and MD5.
	 * <p>
	 * The <code>adk.encryption.key</code> property is required
	 * for encryption methods that use a key. It is this value that will be written
	 * to the @KeyName attribute of the AuthenticationInfoPassword object.
	 * @param zone the zone that is in scope for the current message
	 * @return an instance of SIFEncryption that can write or read passwords using the default
	 * settings.
	 * @throws ADKException If the agent properties do not contain a default encryption 
	 * algorithm or key. The <code>adk.encryption.key</code> property is required 
	 * for encryption methods that use a key. It is this value that will be written
	 * to the @KeyName attribute of the AuthenticationInfoPassword object.
	 * @throws IOException If the key stored in the agent properties cannot be converted 
	 * from BASE64 to binary
	 * @throws NoSuchAlgorithmException Thrown if the specified algorithm is not available. 
	 * For example, RC2 and RSA are not available in the default java 1.4 cipher suites.
	 * @throws NoSuchPaddingException Thrown if the padding method 
	 * (which is always PKCS5Padding) is not available
	 */
	public static SIFEncryption getInstance( Zone zone ) throws 
		ADKException, IOException, NoSuchPaddingException, NoSuchAlgorithmException
	{
		String alg = zone.getProperties().getDefaultEncryptionAlgorithm();
		String keyName = zone.getProperties().getDefaultEncryptionKeyName();
		if( alg == null  )
		{
			throw new ADKException( "The default encryption algorithm or default key name is not defined in the agent or zone properties", zone );
		}
		byte[] key = null;
		if( keyName != null )
		{
		    key = zone.getProperties().getEncryptionKey( keyName );
		}
		// Don't check for a null key at this time because some of the algorithms don't even need a key
		return getInstance( PasswordAlgorithm.wrap( alg ), keyName, key );
	}


	/**
	 * Encrypts the specified password and populates the 
	 * AuthenticationInfoPassword field with the algorithm and key name
	 * values.
	 * @param password The password object to write the encrypted password to
	 * @param value The plain-text password to write to the object
	 * @throws IOException If the value cannot be encoded to base64
	 * @throws InvalidKeyException If the key provided is not valid for the cipher
	 * algorithm
	 * @throws IllegalBlockSizeException 
	 * @throws BadPaddingException
	 */
	public void writePassword( Password password, String value )
		throws IOException, InvalidKeyException, 
		IllegalBlockSizeException, BadPaddingException
	{
		password.setAlgorithm( fAlgorithm );
		password.setKeyName( fkeyName );
	}

	/**
	 * Returns the unencrypted password value from the
	 * AuthenticationInfoPassword field.
	 * <p> 
	 * If the algorithm in use is a hash algorithm, the Base64 
	 * instance of the hash will be returned instead. The application
	 * can check the {@link #isHash()} method to determine of the value
	 * being returned is a hash value or the plain-text password.
	 * @param password The password object to read the password value from
	 * @return The plain-text password or hash value
	 * @throws IOException If a decryption error occurs
	 * @throws InvalidKeyException If a decryption error occurs
	 * @throws IllegalBlockSizeException If a decryption error occurs
	 * @throws BadPaddingException If a decryption error occurs
	 * @throws InvalidAlgorithmParameterException If a decryption error occurs
	 */
	public abstract String readPassword( Password password ) 
			throws IOException, InvalidKeyException, 
			IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException;


	/**
	 * Returns the encryption algorithm that is in use by the class
	 * @return the encryption algorithm that is currently being used
	 */
	public PasswordAlgorithm getAlgorithm()
	{
	    return fAlgorithm;
	}

	/**
	 * Returns the name of the key that is currently being used. This value is the same
	 * as the {@link  Password#getKeyName()}
	 * attribute.
	 * @return the name of the key that is currently being used
	 */
	public String getKeyName()
	{
		return fkeyName;
	}


	/**
	 * Returns the key that the class is currently using to encrypt or decrypt passwords
	 * @return the encryption key
	 */
	public abstract byte[] getKey();


	/**
	 * Returns true if the value is a hashed value and cannot be decrypted. In this case,
	 * the {@link #readPassword(Password)} method will return the hashed value as a Base64 string
	 * @return true if the password is a hashed value. false if it is a plain-text password
	 * that can be decrypted
	 */
	public abstract boolean isHash();

	private static class SIFSymmetricEncryption extends SIFEncryption
	{
		private Cipher fCipher;
		private SecretKeySpec fKeySpec;
		private byte[] fKey;

		SIFSymmetricEncryption( PasswordAlgorithm algorithm, String keyName, String cipherAlgorithm, byte[] key )
			throws NoSuchAlgorithmException, NoSuchPaddingException
		{
		    super( algorithm, keyName );
		    fKey = key;
		    fCipher = Cipher.getInstance( cipherAlgorithm + "/CBC/PKCS5Padding" );
		    fKeySpec = new SecretKeySpec( key, cipherAlgorithm );
		}

		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFEncryption#writePassword(com.edustructures.sifworks.infra.AuthenticationInfoPassword, java.lang.String)
		 */
		@Override
		public void writePassword( Password password, String value )
			throws IOException, InvalidKeyException, 
			IllegalBlockSizeException, BadPaddingException
		{
			super.writePassword( password, value);
			
			fCipher.init( Cipher.ENCRYPT_MODE, fKeySpec );
			byte[] source = value.getBytes( "UTF-8" );
			byte[] encryptedPassword = fCipher.doFinal( source );

			ByteBuffer finalValue = ByteBuffer.allocate( 8 + encryptedPassword.length );
			finalValue.put( fCipher.getIV() );
			finalValue.put( encryptedPassword );
			password.setTextValue( fEncoder.encode(finalValue.array() ) );
		}

				
		@Override
		public String readPassword( Password password )
			throws IOException, InvalidKeyException, 
			IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException
		{
			byte[] encryptedValue = fDecoder.decodeBuffer( password.getTextValue() );
			
			
			// Initialize the IV parameter
			AlgorithmParameterSpec params = null;
			if( this.getAlgorithm().equals( PasswordAlgorithm.RC2 ) )
			{
			    ByteBuffer iv = ByteBuffer.allocate( 8 );
				iv.put( encryptedValue, 0, 8 );
			    params = new RC2ParameterSpec( fKey.length * 8 , iv.array() );
			}	
			else
			{
			    params = new IvParameterSpec( encryptedValue, 0, 8 );
			}
			fCipher.init( Cipher.DECRYPT_MODE, fKeySpec, params );
			byte[] decryptedPassword = fCipher.doFinal( encryptedValue, 8, encryptedValue.length - 8 );
			return new String( decryptedPassword, "UTF-8" );
		}

		
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFEncryption#getKey()
		 */
		@Override
		public byte[] getKey()
		{
			return fKey;
		}

		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFEncryption#isHash()
		 */
		@Override
		public boolean isHash()
		{
			return false;
		}
	}

	/**
	 * Encodes and decodes passwords using BASE64
	 * @author Andrew
	 *
	 */
	private static class SIFClearTextEncryption extends SIFEncryption
	{
		SIFClearTextEncryption( PasswordAlgorithm algorithm, String keyName ){
		    super( algorithm, "" );
		    }

		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFEncryption#writePassword(com.edustructures.sifworks.infra.AuthenticationInfoPassword, java.lang.String)
		 */
		@Override
		public void writePassword( Password password, String value )
			throws IOException, InvalidKeyException, 
			IllegalBlockSizeException, BadPaddingException
		{
			super.writePassword( password, value);
			byte[] clearTextPassword =  value.getBytes( "UTF-8" );
			password.setTextValue( fEncoder.encode( clearTextPassword ) );
		}

	
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFEncryption#readPassword(com.edustructures.sifworks.infra.AuthenticationInfoPassword)
		 */
		@Override
		public String readPassword( Password password )
			throws IOException, InvalidKeyException, 
			IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException
		{
		    byte[] clearTextPassword = fDecoder.decodeBuffer( password.getTextValue() );
			return new String( clearTextPassword, "UTF-8" );
		}

		
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFEncryption#getKey()
		 */
		@Override
		public byte[] getKey()
		{
			return null;
		}

		
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFEncryption#isHash()
		 */
		@Override
		public boolean isHash()
		{
			return false;
		}
	}

	/**
	 * Encodes passwords using a hashing algorithm
	 * @author Andrew
	 */
	private static class SIFHashEncryption extends SIFEncryption
	{
		private MessageDigest fDigestAlgorithm;

		SIFHashEncryption( PasswordAlgorithm algorithm, String keyName, MessageDigest alg )
		{
		    super( algorithm, "" );
		    fDigestAlgorithm = alg;
		}

		
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFEncryption#writePassword(com.edustructures.sifworks.infra.AuthenticationInfoPassword, java.lang.String)
		 */
		@Override
		public void writePassword( Password password, String value )
		throws IOException, InvalidKeyException, 
		IllegalBlockSizeException, BadPaddingException
		{
			super.writePassword( password, value);

			byte[] pass = value.getBytes( "UTF-8" );
			byte[] hashedPassword = fDigestAlgorithm.digest(
				pass );
			password.setTextValue( fEncoder.encode( hashedPassword ));
		}

		
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFEncryption#readPassword(com.edustructures.sifworks.infra.AuthenticationInfoPassword)
		 */
		@Override
		public String readPassword( Password password )
			throws IOException, InvalidKeyException, 
			IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException
		{
			return password.getTextValue();
		}

		
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFEncryption#getKey()
		 */
		@Override
		public byte[] getKey()
		{
			return null;
		}

		
		/* (non-Javadoc)
		 * @see com.edustructures.sifworks.SIFEncryption#isHash()
		 */
		@Override
		public boolean isHash()
		{
		    return true; 
		}

	}

	
	
}
