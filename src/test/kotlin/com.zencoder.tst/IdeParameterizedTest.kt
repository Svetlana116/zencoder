package com.zencoder.tst

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.fixtures.ContainerFixture
import com.intellij.remoterobot.fixtures.JButtonFixture
import com.intellij.remoterobot.fixtures.JTextFieldFixture
import com.intellij.remoterobot.search.locators.byXpath
import io.qameta.allure.Step
import com.intellij.remoterobot.utils.keyboard
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import java.awt.Point
import java.awt.event.KeyEvent
import java.util.concurrent.TimeUnit

@ExtendWith(RemoteRobotExtension::class)
class IdeParameterizedTest {

    private lateinit var remoteRobot: RemoteRobot

    @BeforeEach
    fun setUp(remoteRobot: RemoteRobot) {
        this.remoteRobot = remoteRobot
        Thread.sleep(TimeUnit.SECONDS.toMillis(60))
    }

    @ParameterizedTest(name = "Test run with IDE: {0}, Repository: {1}")
    @CsvFileSource(resources = ["/ide_test_data.csv"], numLinesToSkip = 1)
    fun testIdeFunctionality(
        ideName: String,
        repositoryUrl: String,
        projectName: String,
        fileName: String,
        searchTerm: String
    ) {
        openProjectFromVersionControl(repositoryUrl)
        assertTrue(isProjectOpened(), "Project did not open")
        openFileInProject(projectName, fileName)
        editFileContent("Some text")
        executeCommandInTerminal()
        searchInProject(searchTerm)
    }

    @Step("Open project from version control using URL: {repositoryUrl}")
    private fun openProjectFromVersionControl(repositoryUrl: String) {
        remoteRobot.find(ContainerFixture::class.java, byXpath("//div[@class='ide-frame']")).apply {
            find(JButtonFixture::class.java, byXpath("//div[@text='File']")).click()
            find(JButtonFixture::class.java, byXpath("//div[@text='New']")).click()
            find(JButtonFixture::class.java, byXpath("//div[@text='Project from Version Control...']")).click()
            find(JTextFieldFixture::class.java, byXpath("//div[@class='TextField']")).text = repositoryUrl
            find(JButtonFixture::class.java, byXpath("//div[@text='Clone']")).click()
            Thread.sleep(TimeUnit.SECONDS.toMillis(60))
        }
    }

    @Step("Check if the project is opened")
    private fun isProjectOpened(): Boolean {
        return remoteRobot.find(ContainerFixture::class.java, byXpath("//div[@class='ProjectViewPane']")).isShowing
    }

    @Step("Open file {fileName} in project {projectName}")
    private fun openFileInProject(projectName: String, fileName: String) {
        remoteRobot.find(ContainerFixture::class.java, byXpath("//div[@class='ProjectViewPane']"))
            .doubleClick(Point())
        remoteRobot.keyboard {
            enterText(projectName)
            KeyEvent.VK_ENTER.inv()
            enterText(fileName)
            KeyEvent.VK_ENTER.inv()
        }
    }

    @Step("Edit the content of the file")
    private fun editFileContent(content: String) {
        remoteRobot.find(JTextFieldFixture::class.java, byXpath("//div[@class='EditorComponent']")).text = content
    }

    @Step("Execute command in terminal")
    private fun executeCommandInTerminal() {
        remoteRobot.find(JButtonFixture::class.java, byXpath("//div[@text='View']")).click()
        remoteRobot.find(JButtonFixture::class.java, byXpath("//div[@text='Tool Windows']")).click()
        remoteRobot.find(JButtonFixture::class.java, byXpath("//div[@text='Terminal']")).click()
        remoteRobot.find(JTextFieldFixture::class.java, byXpath("//div[@class='TerminalView']")).text = "pwd\n"
    }

    @Step("Search in project using term: {searchTerm}")
    private fun searchInProject(searchTerm: String) {
        remoteRobot.find(JButtonFixture::class.java, byXpath("//div[@text='Edit']")).click()
        remoteRobot.find(JButtonFixture::class.java, byXpath("//div[@text='Find']")).click()
        remoteRobot.find(JButtonFixture::class.java, byXpath("//div[@text='Find in Files...']")).click()
        remoteRobot.find(JTextFieldFixture::class.java, byXpath("//div[@class='SearchTextField']")).text = searchTerm
        remoteRobot.find(JButtonFixture::class.java, byXpath("//div[@text='Find']")).click()
        remoteRobot.keyboard { KeyEvent.VK_ESCAPE.inv() }
    }
}
