Open ADK Java README
====================

The ADK is licensed under the Apache 2.0 Software License. Copyright in the ADK is held by Pearson and by the community contributors to the ADK project. The license is in the file LICENSE-2.0.txt in the ADK folder after installation. Note that the Apache license does not require you to make any of your application’s code available as open source software.

All files under the Apache license may be redistributed in binary and/or source form with your agent, although only a subset is actually needed by most agents (the contents of the lib directory are usually required). Note that some files may be under the same or a different open source license and subject to third-party license agreements included in the licenses directory.


OVERVIEW
========

The repository for the Open ADK Java project is at: <https://launchpad.net/open-adk-java>

If a Java JDK, Maven, and Bazaar are installed, the following commands will create a branch of the open-adk-java trunk and install the built artifact in the local repository. 

Note that the PROFILE may be one of AU, UK, or US.

* `bzr branch lp:open-adk-java`
* `mvn -P <PROFILE> install`

The ADK jar file (with data model built-in) is deposited in the target folder, or may be used in another maven project using the following dependency coordinates.

    <dependency>
        <groupId>openadk</groupId>
        <artifactId>openadk-library</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <classifier>UK</classifier>
    </dependency>

The classifier is required in order to resolve the artifact that contins the desired data model.


Bazaar VCS Client for Launchpad
===============================

Install Bazaar VCS client for Launchpad.
Instructions on installation, registering branches, etc. can be found at  
<http://doc.bazaar.canonical.com/latest/en/tutorials/ using_bazaar_with_launchpad.html> 

The Bazaar Explorer may be started with `bzr explorer`. The trunk may be checked-out using the Launchpad specific string `lp:open-adk-java`.

When operating from the command line use `bzr branch lp:open-adk-java` to check-out the trunk.


Eclipse IDE Setup
=================

We've found that it is easier to use command line Maven with the Eclipse IDE. To prepare Eclipse for this kind of setup, the M2_REPO classpath variable must be configured first using the following steps.

1. Open Eclipse IDE
2. Open the Eclipse >> Preferences... dialog
3. Navigate to Java >> Build Path >> Classpath Variables
4. Click New...
5. Enter `M2_REPO` for the Name
6. Enter `<HOME>/.m2/repository` for the Path
7. Click OK in the Edit Variable Entry dialog
8. Clock OK in the Preferences dialog

Note: If you find that the M2_REPO variable is already set and unmodifiable, then you likely have m2eclipse installed. Ensure that it is using the correct local repository in this case.

The Maven Eclipse plugin may now be used to create an Eclipse project. From the command line perform the following steps.

1. Change directory to open-adk-java/adk-library
2. Run `mvn -P <PROFILE> eclipse:eclipse`
3. Import the adk-library project into Eclipse as an existing project

Ensure that the `target\generated-sources` exists in the project as a source folder.


Intellij IDEA Setup
===================

Ensure that the Maven plugins for IDEA are enabled before creating a module from the adk-library folder. To create a module in an existing project follow these steps.

1. Click the File >> New Module... menu option
2. Select `Import module from external model`
3. Click Next
4. Select open-adk-java/adk-library as the root search directory
5. Deselect Import Maven project automatically
6. Click Next
7. Select the desirect data model profile (AU, UK, or US)
8. Click Next
9. Select opendadk:openadk-library project to import
10. Click Next

After the module is created in the project, the IDEA Maven Projects panel may be used to trigger Maven lifecycles (clean, compile, etc.).



