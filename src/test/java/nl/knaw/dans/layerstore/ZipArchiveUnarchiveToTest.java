/*
 * Copyright (C) 2024 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
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
package nl.knaw.dans.layerstore;

import org.apache.commons.io.FileUtils;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ZipArchiveUnarchiveToTest extends AbstractTestWithTestDir {
    @Test
    public void should_unarchive_zipfile() throws Exception {
        var zipFile = testDir.resolve("test.zip");
        ZipArchive zipArchive = new ZipArchive(zipFile);
        // Create some files to archive
        Path file1 = testDir.resolve("staging/file1");
        Path file2 = testDir.resolve("staging/path/to/file2");
        Path file3 = testDir.resolve("staging/path/to/file3");

        // Write some string content to the files
        String file1Content = "file1 content";
        String file2Content = "file2 content";
        String file3Content = "file3 content";
        FileUtils.forceMkdir(file2.getParent().toFile());
        FileUtils.write(file1.toFile(), file1Content, "UTF-8");
        FileUtils.write(file2.toFile(), file2Content, "UTF-8");
        FileUtils.write(file3.toFile(), file3Content, "UTF-8");

        // Archive the files
        zipArchive.archiveFrom(testDir.resolve("staging"));

        // Check that the zip file exists
        assertThat(zipFile).exists();
        AssertionsForClassTypes.assertThat(zipArchive.isArchived()).isTrue();

        // Unarchive the files
        zipArchive.unarchiveTo(testDir.resolve("unarchived"));

        // Check that the files are unarchived
        assertThat(file1).exists();
        assertThat(file2).exists();
        assertThat(file3).exists();
    }
}
