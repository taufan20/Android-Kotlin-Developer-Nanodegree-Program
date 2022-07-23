package com.udacity.project4

import com.udacity.project4.locationreminders.data.dto.ReminderDTO


fun getReminderDataItem() = ReminderDTO(
    title = "Title",
    description = "Description",
    location = "Location",
    latitude = 0.0,
    longitude = 0.0
)