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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TarArchiveIsArchivedTest extends AbstractTestWithTestDir {
    @Test
    public void should_change_status_to_archived() throws Exception {
        FileUtils.forceMkdir(stagingDir.toFile());
        var archive = new TarArchive(testDir.resolve("test.tar"));
        archive.archiveFrom(stagingDir);

        assertThat(archive.isArchived()).isTrue();
    }
}
