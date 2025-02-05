Developers Guide
================

See project page ( http://code.google.com/p/ddt/ ) for user information.

#### Setting up the development environment:
 * Clone the Git repository.
 * In Eclipse, use the "import existing projects" wizard, navigate to the Git repository, and add all the Eclipse projects that are present in the root directory of the Git repo. Java Compiler settings should automatically be configured, since each project has project-specific settings stored in source control.
 * Setup the target platform: Open the target platform file: `releng/target-platform/DDT.target` and set it as your target platform.

 
#### Running the tests in Eclipse:

 * The tests are divided into 3 suites for each of the 3 main plugins: DTool, IDE Core, IDE UI.
 * There are common Eclipse launch configurations for each of these test suites, they should already be available in your workspace, and properly configured to run out of the box. Some of the default VM arguments in the launch configuration (already configured) should be:
  * `-DDToolTestResources.workingDir=${workspace_loc}/_runtime-tests` (workspace for tests to use while running)
  * `-DDToolTestResources.baseDir=${workspace_loc:/org.dsource.ddt.dtool/testdata}` (where to get certain DTool test resources. This allows DTool tests to run outside of Eclipse runtime.)
  * Some of the suites (DTool at the moment) can be run in Lite Mode, skipping some of the heavyweight, long-running tests. There is also a launch configuration for this.
  * Some tests may have external runtime dependencies, such as requiring the DUB tool on the environment PATH.

#### Automated Building and Testing:
Using Maven (and Tycho), it is possible to automatically build DDT, create an update site, and run all the tests. Download [Maven](http://maven.apache.org/) (minimum version 3.0), and run the following commands on the root folder of the repository:
 * Run `mvn package` to build the DDT feature into a p2 repository (which is a local update site). It will be placed at `bin-maven/features.repository/repository`
 * Run `mvn integration-test` to build DDT as above and also run the test suites. You can do `mvn integration-test -P TestsLiteMode` to run the test suites in "Lite Mode" (skip certain long-running tests).
 * Also, running `mvn package -P build-ide-product` will build a pre-packaged Eclipse installation with DDT already installed. This is not released to the public, but can potentially be of some use internally.

#### Deploying a new release:
 Releases are made on the p2 update site. The DDT update site is the `updates` Git repository, accessed through plain HTTP: http://updates.ddt.googlecode.com/git/ . Therefore, a new DDT release is created by building a the p2 repository locally as described above (run `mvn integration-test`), then placing the p2 repository in the `https://code.google.com/p/ddt.updates/` Git repository (and pushing to origin of course):
 * The DDT update site is a composite p2 repository, containing the DDT feature repository, and a link to DDT repository dependencies (such as Kepler). This structure should be maintained when updating the repository.
 * There is an Ant script that can help with this task: repo-release-script.xml

Additionally, a new release tag should be created, and the appropriate changelog added to the Github release notes (see [documentation/ChangeLog.md](documentation/ChangeLog.md)). The `latest` tag/branch should also be updated to refer to the new release, so that documentantion links are updated.

## Project design info and notes

#### Old source history:
Old source history of the DDT project can still be found at the [Descent SVN repository](http://svn.dsource.org/projects/descent/!svn/bc/1700/trunk/)

#### About `src-lang/` and `melnorme.lang` code:
The `melnorme.lang` code, or simply Lang code, is IDE functionality not specific to any language, designed to potentially be used by other language IDEs. To achieve this some constraints need to be observed:
 * Lang code can only depend on other `melnorme` code, or on Eclipse.org plugins (including DLTK). But not on IDE specific code.  The only exception to this are the `_Actual` classes, which contain bindings to IDE-specific code (such as ids or other IDE constants, or even methods)
 * Lang code should be place on its own source folder (`src-lang/` usually). This is to make it easier to compare and update the code with the `src-lang/` of another IDE. If the Lang code is identical, only the `_Actual` classes should show up as differences.

Why not re-use Lang code across IDEs by placing it in its own plugin? For two reasons. There are several points where Lang code needs to be connected/bound to certain IDE specific code. So, if Lang code is compiled into plugins shared by IDEs then this binding can only be done at runtime, as opposed to compile-time (This approach is similar to what DLTK does). Second, and perhaps more importantly, sharing at the source level allows unfettered freedom to customize the code. Sharing at a binary level requires that an API be exposed, and sometimes makes it difficult to extend/change functionality that the API didn't foresee changing. (From experience, this has happened a few times when using DLTK).

#### Unit tests double-method wrapper:
 
What is this code idiom seen so often in Junit tests? :
```java
@Test
public void testXXX() throws Exception { testXXX$(); }
public void testXXX$() throws Exception {
```
This is donely solely as an aid when debugging code, so that the "Drop to frame" functionality can be used on the unit-test method. It seems the Eclipse debugger cannot drop-to-frame to a method that is invoked dynamically (such as the unit-test method). So we wrap the unit-test method on another one. So while we now cannot drop-to-frame in `testXXX`, we can do it in `testXXX$`, which basically allows us to restart the unit-test.

TODO: investigate if there is an alternate way to achieve the same. I haven't actually checked that.
