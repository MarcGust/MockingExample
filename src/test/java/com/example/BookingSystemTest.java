package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
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

    @Test
    void shouldThrowExceptionForNullBookingId() {
        assertThatThrownBy(() -> bookingSystem.cancelBooking(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Boknings-id kan inte vara null");
    }

    @ParameterizedTest
    @CsvSource({
            "'2025-01-01T10:00', '2025-01-01T09:00', 'Kan inte avboka påbörjad eller avslutad bokning'"
    })
    void shouldThrowExceptionForCancelingBookingWhenAlreadyStarted(String startTimeStr, String endTimeStr, String expectedMessage) {
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
        LocalDateTime endTime = LocalDateTime.parse(endTimeStr);
        Room room = mock(Room.class);
        Booking booking = mock(Booking.class);
        when(booking.getStartTime()).thenReturn(startTime);
        when(room.hasBooking(anyString())).thenReturn(true);
        when(room.getBooking(anyString())).thenReturn(booking);
        when(roomRepository.findAll()).thenReturn(List.of(room));

        assertThatThrownBy(() -> bookingSystem.cancelBooking("mockBookingId"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(expectedMessage);

        verifyNoInteractions(notificationService);
    }

    @ParameterizedTest
    @CsvSource({
            "'null', '2025-01-01T12:00', 'Måste ange både start- och sluttid'",
            "'2025-01-01T10:00', 'null', 'Måste ange både start- och sluttid'",
            "'2025-01-01T12:00', '2025-01-01T11:00', 'Sluttid måste vara efter starttid'"
    })
    void shouldThrowExceptionForWrongTimeParameters(String startTimeStr, String endTimeStr, String expectedMessage) {
        LocalDateTime startTime = "null".equals(startTimeStr) ? null : LocalDateTime.parse(startTimeStr);
        LocalDateTime endTime = "null".equals(endTimeStr) ? null : LocalDateTime.parse(endTimeStr);

        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @ParameterizedTest
    @CsvSource({
            "'2025-01-01T10:00', '2025-01-01T12:00', 1",
            "'2025-01-01T12:00', '2025-01-01T14:00', 0"
    })
    void shouldReturnAvailableRoomsForCorrectTimeParameters(String startTimeStr, String endTimeStr, int expectedRoomCount) {
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
        LocalDateTime endTime = LocalDateTime.parse(endTimeStr);

        Room room1 = mock(Room.class);
        Room room2 = mock(Room.class);

        when(room1.isAvailable(startTime, endTime)).thenReturn(true);
        when(room2.isAvailable(startTime, endTime)).thenReturn(false);

        if (expectedRoomCount == 0) {
            when(room1.isAvailable(startTime, endTime)).thenReturn(false);
            when(room2.isAvailable(startTime, endTime)).thenReturn(false);
        }

        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));

        List<Room> availableRooms = bookingSystem.getAvailableRooms(startTime, endTime);
        assertThat(availableRooms).hasSize(expectedRoomCount);
    }

    @ParameterizedTest
    @CsvSource({
            "'2025-01-01T10:00', '2025-01-01T12:00', 1",
            "'2025-01-01T12:00', '2025-01-01T14:00', 0"
    })
    void shouldReturnCorrectAvailableRooms(String startTimeString, String endTimeString, int expectedRoomCount) {
        LocalDateTime startTime = LocalDateTime.parse(startTimeString);
        LocalDateTime endTime = LocalDateTime.parse(endTimeString);

        Room room1 = mock(Room.class);
        Room room2 = mock(Room.class);

        when(room1.getName()).thenReturn("Room1");
        when(room2.getName()).thenReturn("Room2");

        when(room1.isAvailable(startTime, endTime)).thenReturn(true);
        when(room2.isAvailable(startTime, endTime)).thenReturn(false);

        if (expectedRoomCount == 0) {
            when(room1.isAvailable(startTime, endTime)).thenReturn(false);
            when(room2.isAvailable(startTime, endTime)).thenReturn(false);
        }

        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));

        List<Room> availableRooms = bookingSystem.getAvailableRooms(startTime, endTime);

        assertThat(availableRooms).hasSize(expectedRoomCount);

        if (expectedRoomCount == 1) {
            assertThat(availableRooms.get(0).getName()).isEqualTo("Room1");
        } else {
            assertThat(availableRooms).isEmpty();
        }
    }

    @ParameterizedTest
    @CsvSource({
            "'null', '2025-01-01T12:00', 'Bokning kräver giltiga start- och sluttider samt rum-id'",
            "'2025-01-01T12:00', 'null', 'Bokning kräver giltiga start- och sluttider samt rum-id'",
            "'null', 'null', 'Bokning kräver giltiga start- och sluttider samt rum-id'",
            "'null', '2025-01-01T12:00', 'Bokning kräver giltiga start- och sluttider samt rum-id'",
    })
    void shouldThrowExceptionWhenParametersAreNull(String startTimeStr, String endTimeStr, String expectedMessage) {
        LocalDateTime startTime = "null".equals(startTimeStr) ? null : LocalDateTime.parse(startTimeStr);
        LocalDateTime endTime = "null".equals(endTimeStr) ? null : LocalDateTime.parse(endTimeStr);
        String roomId = "null".equals(startTimeStr) ? null : "room1";

        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);

        verifyNoInteractions(roomRepository, notificationService);
    }

}
