<title>The template project</title>

<h1>Introduction</h1>

<p>
The template project, that you can download from the <a href="/download">download page</a>,
follows the project structure outlined in the <a href="/tutorial/setup">setup section</a>.
You will notice it features a <code>build.gradle</code> file for letting you build, test, run and deploy you project.
This build file is using the <a href="http://gradle.org">Gradle</a> build system,
which is using the <b>Groovy</b> language for its build description DSL.
</p>

<blockquote>
<b>Note: </b> The template project conveniently provides the
<a href="http://gradle.org/current/docs/userguide/gradle_wrapper.html">Gradle wrapper</a> to run your code without
having to install the Gradle runtime.
</b>
</blockquote>

<a name="build"></a>
<h1>Gradle build file</h1>

<p>
The Gradle build file uses the following Gradle plugins:
</p>

<ul>
<li>
    <b>java</b>: for compiling the Java sources in the <code>src/main/java</code> folder
</li>
<li>
    <b>groovy</b>: for compiling the Groovy sources in the <code>src/main/groovy</code> folder
</li>
<li>
    <b>eclipse</b>: for creating/updating the proper project files for Eclipse
</li>
<li>
    <b>idea</b>: for creating/updating the proper project files for IntelliJ IDEA
</li>
</ul>

<h2>In a nutshell</h2>

<p>
You will find the following Gradle tasks handy:
</p>

<ul>
    <li>
        <tt>gradlew tasks</tt>: to list all the possible tasks which are available
    </li>
    <li>
        <tt>gradlew classes</tt>: to compile your Java/Groovy sources in the folders <code>src/main/java</code> and
        <code>src/main/groovy</code> and have them placed in <code>WEB-INF/classes</code>
    </li>
    <li>
        <tt>gradlew test</tt>: to compile and run your tests from <code>src/test/java</code> and <code>src/test/groovy</code>
    </li>
    <li>
        <tt>gradlew cleanEclipse eclipse</tt>: to generate Eclipse project files
    </li>
    <li>
        <tt>gradlew cleanIdea idea</tt>: to generate IntelliJ project files
    </li>
</ul>

