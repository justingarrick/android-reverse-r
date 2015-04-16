/*
 * Copyright (c) 2015 Justin Garrick
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.”
 */

package com.justingarrick.reverser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class SourceSetTest {
    @Rule
    public TemporaryFolder tempRoot = new TemporaryFolder();

    private static final String PACKAGE_ROOT = "com.domain";
    private static final String ALPHA_DIR = "alpha";
    private static final String BRAVO_DIR = "bravo";
    private static final String ALPHA_PACKAGE = PACKAGE_ROOT + "." + ALPHA_DIR;
    private static final String BRAVO_PACKAGE = PACKAGE_ROOT + "." + BRAVO_DIR;

    private static final String ROOT_CLASS = "Root.java";
    private static final String CLASS_A = "ClassA.java";
    private static final String CLASS_B = "ClassB.java";
    private static final String R_STRING_JAVA = "R$string.java";

    private Path tempRootPath;
    private Path rootClassPath;
    private Path resRootPath;
    private Path clazzAlphaPath;
    private Path resAlphaPath;
    private Path clazzBravoPath;
    private Path resBravoPath;

    @Before public void setup() throws IOException {
        //create directories
        tempRootPath = Paths.get(tempRoot.getRoot().getPath());
        Path alphaPath = Files.createDirectory(Paths.get(tempRootPath + File.separator + ALPHA_DIR));
        Path bravoPath = Files.createDirectory(Paths.get(tempRootPath + File.separator + BRAVO_DIR));

        //create files
        rootClassPath = Files.createFile(Paths.get(tempRootPath + File.separator + ROOT_CLASS));
        resRootPath = Files.createFile(Paths.get(tempRootPath + File.separator + R_STRING_JAVA));
        clazzAlphaPath = Files.createFile(Paths.get(alphaPath + File.separator + CLASS_A));
        resAlphaPath = Files.createFile(Paths.get(alphaPath + File.separator + R_STRING_JAVA));
        clazzBravoPath = Files.createFile(Paths.get(bravoPath + File.separator + CLASS_B));
        resBravoPath = Files.createFile(Paths.get(bravoPath + File.separator + R_STRING_JAVA));

        //write package names to files
        Files.write(resRootPath, createPackageNameEntry(PACKAGE_ROOT).getBytes());
        Files.write(rootClassPath, createPackageNameEntry(PACKAGE_ROOT).getBytes());
        Files.write(clazzAlphaPath, createPackageNameEntry(ALPHA_PACKAGE).getBytes());
        Files.write(resAlphaPath, createPackageNameEntry(ALPHA_PACKAGE).getBytes());
        Files.write(clazzBravoPath, createPackageNameEntry(BRAVO_PACKAGE).getBytes());
        Files.write(resBravoPath, createPackageNameEntry(BRAVO_PACKAGE).getBytes());
    }

    @Test
    public void locatesAllJavaFilesInBravoPackage() {
        SourceSet sourceSet = new SourceSet(tempRootPath);
        Set<Path> files = sourceSet.getJavaFiles(BRAVO_PACKAGE);
        assertThat(files).contains(clazzBravoPath, resBravoPath);
    }

    @Test
    public void locatesAllJavaFilesInSourceSet() {
        SourceSet sourceSet = new SourceSet(tempRootPath);
        Set<Path> files = sourceSet.getJavaFiles();
        assertThat(files).contains(rootClassPath, resRootPath, clazzAlphaPath, resAlphaPath, clazzBravoPath, resBravoPath);
    }

    @Test
    public void locatesAllRFilesInBravoPackage() {
        SourceSet sourceSet = new SourceSet(tempRootPath);
        Set<Path> files = sourceSet.getRFiles(BRAVO_PACKAGE);
        assertThat(files).contains(resBravoPath);
    }

    @Test
    public void locatesAllRFilesInSourceSet() {
        SourceSet sourceSet = new SourceSet(tempRootPath);
        Set<Path> files = sourceSet.getRFiles();
        assertThat(files).contains(resRootPath, resAlphaPath, resBravoPath);
    }

    @Test
    public void locatesClazzBInBravoPackage() {
        boolean found = SourceSet.isInPackage(resBravoPath, BRAVO_PACKAGE);
        assertThat(found).isTrue();
    }

    @Test
    public void doesNotLocateClazzAInBravoPackage() {
        boolean found = SourceSet.isInPackage(resAlphaPath, BRAVO_PACKAGE);
        assertThat(found).isFalse();
    }

    private String createPackageNameEntry(String name) {
        return "package " + name + ";";
    }
}
