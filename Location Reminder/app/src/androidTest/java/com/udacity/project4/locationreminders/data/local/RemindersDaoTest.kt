package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.getReminderDataItem
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt

    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDatabase() = database.close()


    @Test
    fun insertNewReminder_andGetReminders() = runBlockingTest {
        val reminder = getReminderDataItem()
        database.reminderDao().saveReminder(reminder)

        val reminders = database.reminderDao().getReminders()

        assertTrue(reminders.isNotEmpty())

        assertEquals(reminder, reminders[0])
    }

    @Test
    fun insertNewReminder_andSelectById() = runBlockingTest {
        val reminder = getReminderDataItem()
        database.reminderDao().saveReminder(reminder)

        val retrievedReminder = database.reminderDao().getReminderById(reminder.id)

        assertEquals(reminder, retrievedReminder)
    }

    @Test
    fun insertNewReminders_andDeleteAll() = runBlockingTest {
        val reminder = getReminderDataItem()
        database.reminderDao().saveReminder(reminder)
        database.reminderDao().saveReminder(reminder)

        val reminders = database.reminderDao().getReminders()

        assertTrue(reminders.isNotEmpty())

        database.reminderDao().deleteAllReminders()

        val noReminder = database.reminderDao().getReminders()

        assertTrue(noReminder.isEmpty())

    }


}