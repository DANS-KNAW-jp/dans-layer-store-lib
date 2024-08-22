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

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static nl.knaw.dans.layerstore.TestUtils.assumeNotYetFixed;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ZipArchiveUnarchiveToTest extends AbstractTestWithTestDir {
    @Test
    public void should_unarchive_zipfile() throws Exception {
        var zipFile = testDir.resolve("test.zip");
        var zipArchive = new ZipArchive(zipFile);

        createStagingFileWithContent("file1", "file1 content");
        createStagingFileWithContent("path/to/file2", "path/to/file2 content");
        createStagingFileWithContent("path/to/file3", "path/to/file3 content");

        // Archive the files
        zipArchive.archiveFrom(stagingDir);

        // Check that the zip file exists
        assertThat(zipFile).exists();
        assertThat(zipArchive.isArchived()).isTrue();

        // Unarchive the files
        var unarchived = testDir.resolve("unarchived");
        zipArchive.unarchiveTo(unarchived);

        // Check that the files are unarchived
        assertThat(unarchived.resolve("file1")).exists();
        assertThat(unarchived.resolve("path/to/file2")).exists();
        assertThat(unarchived.resolve("path/to/file3")).exists();
    }

    @Test
    public void should_unarchive_zipfile_with_empty_directory() throws Exception {
        var zipFile = testDir.resolve("test.zip");
        var zipArchive = new ZipArchive(zipFile);
        // Create an empty directory to archive
        Path emptyDir = stagingDir.resolve("emptyDir");
        FileUtils.forceMkdir(emptyDir.toFile());

        // Archive the empty directory
        zipArchive.archiveFrom(stagingDir);

        // Unarchive the files
        zipArchive.unarchiveTo(testDir.resolve("unarchived"));
        assertThat(emptyDir).exists();
    }

    @Test
    public void should_report_zip_slip() throws Exception {
        var zipFile = testDir.resolve("test.tar");
        var archive = new ZipArchive(zipFile);

        createStagingFileWithContent("file1", "file1 content");

        // Archive the files
        archive.archiveFrom(stagingDir);

        // Check that the zip file exists
        assertThat(zipFile).exists();
        AssertionsForClassTypes.assertThat(archive.isArchived()).isTrue();

        // add malicious file to the archive
        try (var zip = new ZipArchiveOutputStream(new FileOutputStream(zipFile.toFile()))) {
            var maliciousDir = testDir.resolve("violating/path");
            Files.createDirectories(maliciousDir);
            var entry = new ZipArchiveEntry(maliciousDir, "../" + maliciousDir);
            zip.putArchiveEntry(entry);
            zip.closeArchiveEntry();
        }

        // Unarchive the files
        var unarchived = testDir.resolve("unarchived");
        assertThatThrownBy(() -> archive.unarchiveTo(unarchived))
            .hasCauseInstanceOf(IOException.class)
            .hasRootCauseMessage("Detected Zip Slip: ../target/test/ZipArchiveUnarchiveToTest/violating/path/ in target/test/ZipArchiveUnarchiveToTest/test.tar");
    }

    @Test
    public void should_throw_exception_when_unarchiving_non_existing_zipfile() {
        var zipFile = testDir.resolve("non-existing.zip");
        var zipArchive = new ZipArchive(zipFile);
        assertThat(zipFile).doesNotExist();
        assertThat(zipArchive.isArchived()).isFalse();
        assertThatThrownBy(() -> zipArchive.unarchiveTo(testDir.resolve("unarchived")))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Could not unarchive target/test/ZipArchiveUnarchiveToTest/non-existing.zip")
            .hasCauseInstanceOf(NoSuchFileException.class)
            .hasRootCauseMessage("target/test/ZipArchiveUnarchiveToTest/non-existing.zip");
    }

    @Test
    public void should_throw_exception_when_unarchiving_to_non_empty_directory() throws Exception {
        var zipFile = testDir.resolve("test.zip");
        var zipArchive = new ZipArchive(zipFile);

        // Create a file to archive
        Files.createDirectories(stagingDir);
        Files.writeString(stagingDir.resolve("file1"), "file1 content");
        zipArchive.archiveFrom(stagingDir);

        Files.createDirectories(testDir.resolve("unarchived/content"));

        assumeNotYetFixed("unarchiveTo does not check if the target directory exists");
        assertThatThrownBy(() -> zipArchive.unarchiveTo(testDir.resolve("unarchived")));
    }
}
