package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class BookingSystemTest {

    private BookingSystem bookingSystem;
    private AutoCloseable mocks;

    @Mock
    private RoomRepository roomRepository;
    @Mock
    private TimeProvider timeProvider;
    @Mock
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        bookingSystem = new BookingSystem(timeProvider, roomRepository, notificationService);

        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void shouldBookRoomSuccessfully() throws NotificationException {
        String roomId = "room1";
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        LocalDateTime endTime = startTime.plusHours(1);

        Room room = mock(Room.class);
        when(room.isAvailable(startTime, endTime)).thenReturn(true);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        boolean bookedRoom = bookingSystem.bookRoom(roomId, startTime, endTime);

        assertThat(bookedRoom).isTrue();
        verify(room).addBooking(any(Booking.class));
        verify(roomRepository).save(room);
        verify(notificationService).sendBookingConfirmation(any(Booking.class));
    }

    @Test
    void shouldThrowExceptionForWrongTime() {
        String roomId = "room1";
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.minusHours(1);

        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sluttid måste vara efter starttid");

        verifyNoInteractions(roomRepository, notificationService);
    }

    @Test
    void shouldThrowExceptionForWrongRoomId() {
        String roomId = "roomX";
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);

        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rummet existerar inte");

        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldBookRoomSuccessfullyButStillThrowNotificationException() throws NotificationException {
        String roomId = "room1";
        LocalDateTime startTime = LocalDateTime.now().plusHours(2);
        LocalDateTime endTime = startTime.plusHours(1);

        Room room = mock(Room.class);
        when(room.isAvailable(startTime, endTime)).thenReturn(true);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        doThrow(new NotificationException("Test exception")).when(notificationService).sendBookingConfirmation(any(Booking.class));

        boolean bookedRoom = bookingSystem.bookRoom(roomId, startTime, endTime);

        assertThat(bookedRoom).isTrue();
        verify(room).addBooking(any(Booking.class));
        verify(roomRepository).save(room);
        verify(notificationService).sendBookingConfirmation(any(Booking.class));
    }
}

