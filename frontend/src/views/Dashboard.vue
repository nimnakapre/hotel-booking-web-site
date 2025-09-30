<template>
  <section>
    <div class="dashboard-header">
      <h2 class="dashboard-title">My Bookings</h2>
      <div class="dashboard-actions">
        <RouterLink to="/bookings/new" class="new-booking-btn">
          <span>+</span>
          <span>New Booking</span>
        </RouterLink>
      </div>
    </div>
    <div class="dashboard-grid">
      <div class="dashboard-card">
        <h3>Upcoming Bookings</h3>
        <div class="dashboard-card-content">
          <div v-if="!upcoming.length" class="no-bookings">
            No upcoming bookings
          </div>
          <div
            v-for="b in upcoming"
            :key="b.id"
            class="booking-item"
            @click="showBookingModal(b)"
          >
            <div class="booking-info">
              <div class="booking-room">Room #{{ b.room.id }}</div>
              <div class="booking-dates">
                {{ b.checkInDate }} → {{ b.checkOutDate }}
              </div>
              <div class="booking-amount">${{ b.totalAmount }}</div>
            </div>
            <div class="actions">
              <button
                v-if="b.status !== 'CANCELLED'"
                @click.stop="cancel(b.id)"
                class="cancel-btn"
              >
                Cancel
              </button>
              <span v-else class="canceled-status">Canceled</span>
            </div>
          </div>
        </div>
      </div>
      <div class="dashboard-card">
        <h3>Past Bookings</h3>
        <div class="dashboard-card-content">
          <div v-if="!past.length" class="no-bookings">No past bookings</div>
          <div
            v-for="b in past"
            :key="b.id"
            class="booking-item"
            @click="showBookingModal(b)"
          >
            <div class="booking-info">
              <div class="booking-room">Room #{{ b.room.id }}</div>
              <div class="booking-dates">
                {{ b.checkInDate }} → {{ b.checkOutDate }}
              </div>
              <div class="booking-amount">${{ b.totalAmount }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Booking Details Modal -->
    <div v-if="selectedBooking" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>Booking Details</h3>
          <button @click="closeModal" class="modal-close">&times;</button>
        </div>

        <div class="modal-body">
          <div class="booking-detail-section">
            <h4>Guest Information</h4>
            <div class="detail-grid" v-if="selectedBooking.contactInformation">
              <div class="detail-item">
                <span class="label">Title:</span>
                <span class="value">{{
                  selectedBooking.contactInformation.title
                }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Full Name:</span>
                <span class="value">{{
                  selectedBooking.contactInformation.fullName
                }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Email:</span>
                <span class="value">{{
                  selectedBooking.contactInformation.email
                }}</span>
              </div>
              <div
                class="detail-item"
                v-if="selectedBooking.contactInformation.phone"
              >
                <span class="label">Phone:</span>
                <span class="value">{{
                  selectedBooking.contactInformation.phone
                }}</span>
              </div>
            </div>
            <div v-else class="no-contact-info">
              <p>No contact information provided for this booking.</p>
            </div>
          </div>

          <div class="booking-detail-section">
            <h4>Stay Information</h4>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="label">Check-in:</span>
                <span class="value">{{
                  formatDate(selectedBooking.checkInDate)
                }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Check-out:</span>
                <span class="value">{{
                  formatDate(selectedBooking.checkOutDate)
                }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Guests:</span>
                <span class="value">{{ selectedBooking.numberOfGuests }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Nights:</span>
                <span class="value"
                  >{{
                    calculateNights(
                      selectedBooking.checkInDate,
                      selectedBooking.checkOutDate
                    )
                  }}
                  night{{
                    calculateNights(
                      selectedBooking.checkInDate,
                      selectedBooking.checkOutDate
                    ) > 1
                      ? "s"
                      : ""
                  }}</span
                >
              </div>
              <div class="detail-item">
                <span class="label">Status:</span>
                <span
                  class="value status"
                  :class="selectedBooking.status.toLowerCase()"
                  >{{ selectedBooking.status }}</span
                >
              </div>
            </div>
          </div>

          <div class="booking-detail-section">
            <h4>Room Information</h4>
            <div class="room-detail-card">
              <div class="room-image-container">
                <img
                  v-if="selectedBooking.room.imageName"
                  :src="`/src/room_img/${selectedBooking.room.imageName}`"
                  :alt="`${selectedBooking.room.roomType} room`"
                  class="room-detail-image"
                />
                <div v-else class="room-detail-placeholder">
                  <span>🏨</span>
                </div>
              </div>
              <div class="room-detail-info">
                <div class="room-detail-name">
                  {{ selectedBooking.room.roomType }}
                </div>
                <div class="room-detail-number">
                  Room #{{ selectedBooking.room.roomNumber }}
                </div>
                <div class="room-detail-description">
                  {{ selectedBooking.room.description }}
                </div>
                <div class="room-detail-capacity">
                  {{ selectedBooking.room.capacity }} guests
                </div>
              </div>
            </div>
          </div>

          <div class="booking-detail-section">
            <h4>Pricing Breakdown</h4>
            <div class="pricing-breakdown">
              <div class="price-item">
                <span class="label">Room Price:</span>
                <span class="value"
                  >${{ selectedBooking.room.pricePerNight }}/night</span
                >
              </div>
              <div class="price-item">
                <span class="label">Nights:</span>
                <span class="value">{{
                  calculateNights(
                    selectedBooking.checkInDate,
                    selectedBooking.checkOutDate
                  )
                }}</span>
              </div>
              <div class="price-item">
                <span class="label">Subtotal:</span>
                <span class="value"
                  >${{
                    selectedBooking.room.pricePerNight *
                    calculateNights(
                      selectedBooking.checkInDate,
                      selectedBooking.checkOutDate
                    )
                  }}</span
                >
              </div>
              <div class="price-item">
                <span class="label">Tax (10%):</span>
                <span class="value"
                  >${{
                    Math.round(
                      selectedBooking.room.pricePerNight *
                        calculateNights(
                          selectedBooking.checkInDate,
                          selectedBooking.checkOutDate
                        ) *
                        0.1 *
                        100
                    ) / 100
                  }}</span
                >
              </div>
              <div class="price-item total">
                <span class="label">Total Amount:</span>
                <span class="value">${{ selectedBooking.totalAmount }}</span>
              </div>
            </div>
          </div>

          <div class="booking-detail-section">
            <h4>Booking Timeline</h4>
            <div class="timeline-info">
              <div class="timeline-item">
                <span class="label">Created:</span>
                <span class="value">{{
                  formatDateTime(selectedBooking.createdAt)
                }}</span>
              </div>
              <div class="timeline-item">
                <span class="label">Last Updated:</span>
                <span class="value">{{
                  formatDateTime(selectedBooking.updatedAt)
                }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button @click="closeModal" class="btn-secondary">Close</button>
          <button
            v-if="
              selectedBooking.status === 'PENDING' ||
              selectedBooking.status === 'CONFIRMED'
            "
            @click="cancelBooking(selectedBooking.id)"
            class="btn-danger"
          >
            Cancel Booking
          </button>
        </div>
      </div>
    </div>

    <!-- Cancel Confirmation Modal -->
    <div v-if="showCancelModal" class="modal-overlay" @click="closeCancelModal">
      <div class="modal-content cancel-confirmation-modal" @click.stop>
        <div class="modal-header">
          <h3>Cancel Booking</h3>
          <button @click="closeCancelModal" class="modal-close">&times;</button>
        </div>
        <div class="modal-body">
          <p>Are you sure you want to cancel this booking?</p>
          <div class="booking-summary" v-if="bookingToCancel">
            <div class="summary-item">
              <span class="label">Room:</span>
              <span class="value"
                >Room #{{ bookingToCancel.room.id }} -
                {{ bookingToCancel.room.roomType }}</span
              >
            </div>
            <div class="summary-item">
              <span class="label">Dates:</span>
              <span class="value"
                >{{ formatDate(bookingToCancel.checkInDate) }} →
                {{ formatDate(bookingToCancel.checkOutDate) }}</span
              >
            </div>
            <div class="summary-item">
              <span class="label">Guests:</span>
              <span class="value">{{ bookingToCancel.numberOfGuests }}</span>
            </div>
          </div>
          <p class="warning-text">This action cannot be undone.</p>
        </div>
        <div class="modal-footer">
          <button @click="closeCancelModal" class="btn-secondary">
            Keep Booking
          </button>
          <button @click="confirmCancel" class="btn-danger">
            Yes, Cancel Booking
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import api from "../utils/api";
import { RouterLink } from "vue-router";

const upcoming = ref<any[]>([]);
const past = ref<any[]>([]);
const selectedBooking = ref<any>(null);
const showCancelModal = ref(false);
const bookingToCancel = ref<any>(null);

async function load() {
  try {
    const [u, p] = await Promise.all([
      api.get("/bookings/upcoming"),
      api.get("/bookings/past"),
    ]);
    upcoming.value = u.data;
    past.value = p.data;
  } catch (error: any) {
    console.error("Error loading bookings:", error);
    if (error.response?.status === 401) {
      // Token expired or invalid - will be handled by API interceptor
      return;
    }
    if (error.response?.status === 403) {
      // Permission denied - show user-friendly message
      console.warn(
        "Access denied to bookings. You may not have permission to view bookings."
      );
      return;
    }
    alert("Failed to load bookings. Please try again.");
  }
}

function showCancelConfirmation(booking: any) {
  bookingToCancel.value = booking;
  showCancelModal.value = true;
}

function closeCancelModal() {
  showCancelModal.value = false;
  bookingToCancel.value = null;
}

async function confirmCancel() {
  if (!bookingToCancel.value) return;

  try {
    await api.put(`/bookings/${bookingToCancel.value.id}/cancel`);
    await load();
    closeCancelModal();
  } catch (error: any) {
    console.error("Error canceling booking:", error);
    if (error.response?.status === 401) {
      // Token expired or invalid - will be handled by API interceptor
      return;
    }
    if (error.response?.status === 403) {
      // Permission denied
      alert("You don't have permission to cancel this booking.");
      return;
    }
    alert("Failed to cancel booking. Please try again.");
  }
}

async function cancel(id: number) {
  // This function is kept for backward compatibility but now shows confirmation
  const booking = [...upcoming.value, ...past.value].find((b) => b.id === id);
  if (booking) {
    showCancelConfirmation(booking);
  }
}

function showBookingModal(booking: any) {
  selectedBooking.value = booking;
}

function closeModal() {
  selectedBooking.value = null;
}

async function cancelBooking(id: number) {
  await cancel(id);
  closeModal();
}

function formatDate(dateString: string) {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleDateString("en-US", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
}

function formatDateTime(dateString: string) {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleString("en-US", {
    year: "numeric",
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function calculateNights(checkIn: string, checkOut: string) {
  if (!checkIn || !checkOut) return 0;
  const start = new Date(checkIn);
  const end = new Date(checkOut);
  const diffTime = end.getTime() - start.getTime();
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
}

onMounted(load);
</script>
