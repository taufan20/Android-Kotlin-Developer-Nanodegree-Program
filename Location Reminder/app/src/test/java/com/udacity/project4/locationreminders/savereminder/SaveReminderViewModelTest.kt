package com.udacity.project4.locationreminders.savereminder

import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.P])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    //TODO: provide testing to the SaveReminderView and its live data objects

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel

    @Before
    fun setup() {
        dataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

    @After
    fun stoppingKoin() {
        stopKoin()
    }

    @Test
    fun saveReminder_showSuccessToast() = mainCoroutineRule.runBlockingTest {

        viewModel.saveReminder(getFakeReminderDataItem())
        assertThat(viewModel.showToast.getOrAwaitValue(),
            `is`(ApplicationProvider.getApplicationContext<Context>().getString(R.string.reminder_saved)))
    }

    @Test
    fun validateNewReminderWithNoTitle_showErrorSnackbar() = mainCoroutineRule.runBlockingTest {
        val reminderDataItem = getFakeReminderDataItem()
        reminderDataItem.title = null
        val isValid = viewModel.validateEnteredData(reminderDataItem)

        assertThat(isValid, `is`(false))

        assertThat(viewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_enter_title))
    }

    @Test
    fun validateNewReminderWithNoLocation_showErrorSnackbar() = mainCoroutineRule.runBlockingTest {
        val reminderDataItem = getFakeReminderDataItem()
        reminderDataItem.location = null
        val isValid = viewModel.validateEnteredData(reminderDataItem)

        assertThat(isValid, `is`(false))

        assertThat(viewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_select_location))
    }

    @Test
    fun validateNewReminderWithCompleteData_returnTrue() = mainCoroutineRule.runBlockingTest {
        val reminderDataItem = getFakeReminderDataItem()
        val isValid = viewModel.validateEnteredData(reminderDataItem)

        assertThat(isValid, `is`(true))
    }

    @Test
    fun saveNewReminder_showLoading() = mainCoroutineRule.runBlockingTest {
        mainCoroutineRule.pauseDispatcher()

        val reminderDataItem = getFakeReminderDataItem()
        viewModel.validateAndSaveReminder(reminderDataItem)
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    private fun getFakeReminderDataItem() = ReminderDataItem(
        title = "Title",
        description = "Description",
        location = "Location",
        latitude = 0.0,
        longitude = 0.0
    )

}