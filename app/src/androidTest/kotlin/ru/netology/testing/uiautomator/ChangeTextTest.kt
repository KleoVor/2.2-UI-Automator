package ru.netology.testing.uiautomator

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// Константы для использования в тестах
const val SETTINGS_PACKAGE = "com.android.settings" // Пакет настроек Android
const val MODEL_PACKAGE = "ru.netology.testing.uiautomator" // Пакет под тестирование
const val TIMEOUT = 5000L // Время ожидания в миллисекундах

// Указываем, что тесты будут выполнены с использованием AndroidJUnit4
@RunWith(AndroidJUnit4::class)
class ChangeTextTest {

    private lateinit var device: UiDevice // Объект для взаимодействия с устройством
    private val textToSet = "Netology" // Текст для проверки корректного ввода
    private val emptyText = "" // Пустая строка для тестов
    private val textToSet1 = "     " // Строка, состоящая только из пробелов

    // Метод для ожидания, пока пакет приложения станет доступен
    private fun waitForPackage(packageName: String) {
        val context = ApplicationProvider.getApplicationContext<Context>() // Получаем контекст приложения
        val intent = context.packageManager.getLaunchIntentForPackage(packageName) // Получаем интент для запуска приложения
        context.startActivity(intent) // Запускаем приложение
        device.wait(Until.hasObject(By.pkg(packageName)), TIMEOUT) // Ждем, пока приложение станет доступно
    }

    // Подготовка перед каждым тестом: инициализация устройства и ожидание запуска лаунчера
    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()) // Получаем экземпляр UiDevice
        device.pressHome() // Нажимаем кнопку Home
        val launcherPackage = device.launcherPackageName // Получаем имя пакета лаунчера
        device.wait(Until.hasObject(By.pkg(launcherPackage)), TIMEOUT) // Ждем, пока лаунчер станет доступным
    }

    // Метод для ввода текста и нажатия кнопки изменения текста
    private fun inputTextAndClick(userInput: String): String {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        // Вводим текст в поле ввода
        device.findObject(By.res(packageName, "userInput")).text = userInput
        device.findObject(By.res(packageName, "buttonChange")).click()
        // Возвращаем текст, который отображается в элементе textToBeChanged
        return device.findObject(By.res(packageName, "textToBeChanged")).text
    }

    // Метод для получения текущего текста из элемента textToBeChanged
    private fun getCurrentText(): String {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)
        // Возвращаем текст из элемента textToBeChanged
        return device.findObject(By.res(packageName, "textToBeChanged")).text
    }

    // Метод для ввода текста и открытия нового Activity
    private fun inputTextAndOpenNewActivity(inputText: String) {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        // Вводим текст в поле ввода
        device.findObject(By.res(packageName, "userInput")).text = inputText
        device.findObject(By.res(packageName, "buttonActivity")).click()

        // Ждем, пока новое Activity откроется
        device.wait(Until.hasObject(By.res(packageName, "text")), TIMEOUT)
    }

    // Тестирование: пустой ввод не должен изменять текст
    @Test
    fun testEmptyTextInputDoesNotChangeOutput() {
        val initialResult = getCurrentText()
        val result = inputTextAndClick(emptyText)

        assertEquals(initialResult, result)
    }

    // Тестирование: корректный ввод текста должен изменять текст
    @Test
    fun testValidTextInputChangesOutput() {
        val result = inputTextAndClick(textToSet)
        assertEquals(result, textToSet)
    }

    // Тестирование: изменение текста и открытие нового Activity
    @Test
    fun testChangeTextAndOpenNewActivity() {
        val inputText = textToSet
        inputTextAndOpenNewActivity(inputText)
        val resultText = device.findObject(By.res(MODEL_PACKAGE, "text")).text
        assertEquals(inputText, resultText)
    }

    // Тестирование: попытка изменить текст на пустую строку и открыть новое Activity
    @Test
    fun testChangeTextEmptyTextAndOpenNewActivity() {
        val inputText = textToSet1
        inputTextAndOpenNewActivity(inputText)

        val resultText = device.findObject(By.res(MODEL_PACKAGE, "text")).text
        assertEquals(inputText, resultText)
    }
}