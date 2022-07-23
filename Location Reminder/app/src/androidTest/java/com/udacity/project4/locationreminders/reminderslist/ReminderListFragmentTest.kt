package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.getReminderDataItem
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.koin.core.context.GlobalContext.get

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

//    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.

    private lateinit var repository: RemindersLocalRepository

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initRepository() {
        stopKoin()

        val myModule = module {
            viewModel {
                RemindersListViewModel(getApplicationContext(), get() as ReminderDataSource)
            }

            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(getApplicationContext()) }

        }
        startKoin {
            androidContext(getApplicationContext())
            modules(listOf(myModule))
        }

        repository = get().koin.get()

        runBlocking {
            repository.deleteAllReminders()
        }
    }


    @Test
    fun noReminders_showNoDataMessage() = runBlockingTest {
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withId(R.id.noDataTextView)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @Test
    fun hasReminders_showReminderList() = runBlockingTest {

        val reminder: ReminderDTO = getReminderDataItem()

        repository.saveReminder(reminder)
        repository.saveReminder(reminder)

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withId(R.id.noDataTextView)).check(
            ViewAssertions.matches(
                CoreMatchers.not(
                    ViewMatchers.isDisplayed()
                )
            )
        )

        onView(ViewMatchers.withText(reminder.title)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        onView(ViewMatchers.withText(reminder.description)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

        onView(ViewMatchers.withText(reminder.location)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

    }

    @Test
    fun clickOnFabIcon_navigatesTo_saveReminderFragment() {
        val scenario =
            launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }


}