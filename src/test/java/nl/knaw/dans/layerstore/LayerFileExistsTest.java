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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LayerFileExistsTest extends AbstractTestWithTestDir {

    @Test
    public void should_return_true_if_file_exists() throws Exception {
        var stagingDir = testDir.resolve("staging");
        var layer = new LayerImpl(1, stagingDir, new ZipArchive(testDir.resolve("test.zip")));
        if (!stagingDir.resolve("path/to").toFile().mkdirs() ||
            !stagingDir.resolve("path/to/file1").toFile().createNewFile() ||
            !stagingDir.resolve("path/to/file2").toFile().createNewFile()) {
            throw new Exception("Could not create files to delete");
        }

        assertThat(layer.fileExists("path/to/file1")).isTrue();
    }

    @Test
    public void should_return_false_if_file_does_not_exist() throws Exception {
        var stagingDir = testDir.resolve("staging");
        var layer = new LayerImpl(1, stagingDir, new ZipArchive(testDir.resolve("test.zip")));
        if (!stagingDir.resolve("path/to").toFile().mkdirs() ||
            !stagingDir.resolve("path/to/file1").toFile().createNewFile() ||
            !stagingDir.resolve("path/to/file2").toFile().createNewFile()) {
            throw new Exception("Could not create files to delete");
        }

        assertThat(layer.fileExists("path/to/file3")).isFalse();
    }
}
