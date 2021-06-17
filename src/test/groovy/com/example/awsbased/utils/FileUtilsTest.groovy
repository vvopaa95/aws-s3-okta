package com.example.awsbased.utils


import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.spi.FileSystemProvider

class FileUtilsTest extends Specification {

    def fileUtils = new FileUtils()

    def "should successfully transfer to temp file"() {
        given:
            def multipartFile = Mock(MultipartFile)
            def prefix = "prefix"
            def suffix = "suffix"
            def fileName = "WOW"
        when:
            def resultTuple2 = fileUtils.transferToTempFile(multipartFile, prefix, suffix)
        then:
            1 * multipartFile.transferTo(_)
            1 * multipartFile.getOriginalFilename() >> fileName
            0 * _
        and:
            fileName == resultTuple2.getFirst()
            def tmpFilePath = resultTuple2.getSecond()
            tmpFilePath
        and: "clean tmp file successfully"
            Files.deleteIfExists(tmpFilePath)
    }

    def "should catch IOException when transfer to temp file"() {
        given:
            def multipartFile = Mock(MultipartFile)
            def prefix = "prefix"
            def suffix = "suffix"
            def fileName = "WOW"
        when:
            def resultTuple2 = fileUtils.transferToTempFile(multipartFile, prefix, suffix)
        then:
            1 * multipartFile.transferTo(_ as Path) >> { Path path ->
                assert Files.deleteIfExists(path)
                throw new IOException()
            }
            1 * multipartFile.getOriginalFilename()
            1 * multipartFile.getName() >> fileName
            0 * _
        and:
            !resultTuple2
    }

    def "should delete tmp file successfully"() {
        given:
            def tmpFile = Files.createTempFile("a", "b")
        when:
            def isDeleted = fileUtils.deleteFile(tmpFile)
        then:
            isDeleted
    }

    def "should catch IOException when delete file"() {
        given:
            def path = Mock(Path)
            def fileSystem = Mock(FileSystem)
            def fileSystemProvider = Mock(FileSystemProvider)
        when:
            def isDeleted = fileUtils.deleteFile(path)
        then:
            1 * path.getFileSystem() >> fileSystem
            1 * fileSystem.provider() >> fileSystemProvider
            1 * fileSystemProvider.deleteIfExists(path) >> {
                throw new IOException()
            }
            0 * _
        and:
            !isDeleted
    }
}
