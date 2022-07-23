package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.getReminderDataItem
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    TODO: Add testing implementation to the RemindersLocalRepository.kt

    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initRepository() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun closeDatabase() = database.close()


    @Test
    fun insertNewReminder_andGetReminders() = runBlocking {
        val reminder = getReminderDataItem()
        repository.saveReminder(reminder)

        val result = repository.getReminders()
        val reminders = (result as Result.Success<List<ReminderDTO>>).data

        Assert.assertTrue(reminders.isNotEmpty())

        Assert.assertEquals(reminder, reminders[0])
    }


    @Test
    fun insertNewReminder_andSelectById() = runBlocking {
        val reminder = getReminderDataItem()
        repository.saveReminder(reminder)

        val result = repository.getReminder(reminder.id)
        val retrievedReminder = (result as Result.Success<ReminderDTO>).data

        Assert.assertEquals(reminder, retrievedReminder)
    }

    @Test
    fun insertNewReminders_andDeleteAll() = runBlocking {
        val reminder = getReminderDataItem()
        repository.saveReminder(reminder)
        repository.saveReminder(reminder)

        var result = repository.getReminders()
        val reminders = (result as Result.Success<List<ReminderDTO>>).data

        Assert.assertTrue(reminders.isNotEmpty())

        repository.deleteAllReminders()

        result = repository.getReminders()
        val noReminder = (result as Result.Success<List<ReminderDTO>>).data

        Assert.assertTrue(noReminder.isEmpty())

    }

    @Test
    fun insertNewReminders_andSelectByIdDoesNotExist() = runBlocking {
        val result: Result.Error = repository.getReminder("does_not_exist") as Result.Error

        Assert.assertEquals("Reminder not found!", result.message)
    }



}