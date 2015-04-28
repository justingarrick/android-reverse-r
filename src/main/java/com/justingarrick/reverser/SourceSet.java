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
 * limitations under the License.
 */

package com.justingarrick.reverser;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represents a collection of source files.
 */
public class SourceSet {

    /* Pattern to match R.java, R$anim.java, etc. */
    private static final Pattern R_FILES = Pattern.compile("R(\\$[a-z]+)?\\.java", Pattern.CASE_INSENSITIVE);

    /* Root directory of the source set */
    private Path rootDir;

    public SourceSet(Path rootDir) {
        this.rootDir = rootDir;
    }

    /**
     * Returns a set of java files located in this source set
     */
    public Set<Path> getJavaFiles() {
        try {
            return Files.walk(rootDir)
                    .filter(path -> path.toString().toLowerCase().endsWith(".java"))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            return new HashSet<>();
        }
    }

    /**
     * Returns a set of .java files for a specific package in this
     * source set
     *
     * @param packageName of the package to search
     * @return the set of paths representing all the relevant java files
     */
    public Set<Path> getJavaFiles(String packageName) {
        return getJavaFiles().stream()
                .filter(path -> isInPackage(path, packageName))
                .collect(Collectors.toSet());
    }

    /**
     * Find R.java, R$anim.java, etc. files in this source set
     *
     * @return the set of paths representing all of the r files in this source set
     */
    public Set<Path> getRFiles() {
        try {
            return Files.walk(rootDir)
                    .filter(path -> R_FILES.matcher(path.getFileName().toString()).matches())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            return new HashSet<>();
        }
    }

    /**
     * Find R.java, R$anim.java, etc. files in a specific package in this
     * source set.
     *
     * @param packageName of the package to search
     * @return the set of paths representing all of the relevant r files
     */
    public Set<Path> getRFiles(String packageName) {
        return getRFiles().stream()
                .filter(path -> isInPackage(path, packageName))
                .collect(Collectors.toSet());
    }

    /**
     * Checks to see if the .java file at the provided path
     * is in the package specified on the command line.
     * <p/>
     * NOTE: Would love for this to be done with JavaParser/ASM,
     * but since de-obfuscated code has labels, etc. that choke the
     * library, it's best to check for this with a regex and
     * only use JavaParser/ASM on the relatively standard
     * format of the R files.
     *
     * @param path a path to a .java file
     * @return true if the .java file is in the package, false otherwise
     */
    public static boolean isInPackage(Path path, String packageName) {
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8).stream()
                    .anyMatch(line -> line.matches("(?i)package\\s+" + Pattern.quote(packageName) + ".*;"));
        } catch (IOException e) {
            return false;
        }
    }
}
