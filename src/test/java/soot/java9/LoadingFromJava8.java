package soot.java9;

import com.google.common.base.Optional;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.SootModuleResolver;
import soot.options.Options;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

@Category(categories.Java9Test.class)
public class LoadingFromJava8 {
    @Test
    public void testLoadingJava9Class() {
        G.v().reset();
        Options.v().set_soot_classpath("VIRTUAL_FS_FOR_JDK");
        //TODO: Somehow i need to tell Soot where that jrt-fs.jar is.
        //  Using set_soot_modulepath() seems just to make things worse

        VirtualFileSystemUsage();

        Scene.v().loadBasicClasses();

        SootClass klass1
                = SootModuleResolver.v().resolveClass("java.lang.invoke.VarHandle", SootClass.BODIES, Optional.of("java.base"));

        assertTrue(klass1.getName().equals("java.lang.invoke.VarHandle"));
        assertTrue(klass1.moduleName.equals("java.base"));

        SootClass klass2 = SootModuleResolver.v().resolveClass("java.lang.invoke.ConstantBootstraps", SootClass.BODIES,
                Optional.of("java.base"));

        assertTrue(klass2.getName().equals("java.lang.invoke.ConstantBootstraps"));
        assertTrue(klass2.moduleName.equals("java.base"));

        Scene.v().loadNecessaryClasses();
    }

    private static void VirtualFileSystemUsage()
    {
        try
        {
            Path pathToJRE = Paths.get("D:\\Java\\openjdk-11.0.5_10");
            Path p = pathToJRE.resolve("lib").resolve("jrt-fs.jar");
            if(Files.exists(p))
            {
                try(URLClassLoader loader = new URLClassLoader(new URL[]{ p.toUri().toURL() });
                    FileSystem fs = FileSystems.newFileSystem(URI.create("jrt:/"),
                            Collections.emptyMap(),
                            loader)) {
                    System.out.println("Modules of "+pathToJRE);
                    Files.list(fs.getPath("/modules")).forEach(System.out::println);
                    System.out.println();
                }


            }
        }
        catch (Exception e1)
        {
            System.out.println(e1.getMessage());
        }
    }
}
