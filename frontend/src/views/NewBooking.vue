<template>
  <div class="booking-stepper">
    <!-- Step Progress Indicator -->
    <div class="stepper-header">
      <div class="stepper-progress">
        <div
          v-for="(step, index) in steps"
          :key="index"
          class="step-item"
          :class="{
            active: currentStep === index + 1,
            completed: currentStep > index + 1,
          }"
        >
          <div class="step-number">{{ index + 1 }}</div>
          <div class="step-title">{{ step.title }}</div>
        </div>
      </div>
    </div>

    <!-- Step Content -->
    <div class="stepper-content">
      <!-- Step 1: Room Search -->
      <div v-if="currentStep === 1" class="step-card">
        <h2>Search Available Rooms</h2>
        <form @submit.prevent="searchRooms" class="search-form">
          <div class="search-row">
            <div class="search-inputs">
              <div class="form-group">
                <label>Check-in Date</label>
                <input type="date" v-model="checkIn" :min="minDate" required />
              </div>
              <div class="form-group">
                <label>Check-out Date</label>
                <input
                  type="date"
                  v-model="checkOut"
                  :min="minCheckOutDate"
                  required
                />
              </div>
              <div class="form-group">
                <label>Number of Guests</label>
                <input type="number" min="1" v-model.number="guests" required />
              </div>
            </div>
            <div class="search-button-container">
              <label>&nbsp;</label>
              <button
                type="submit"
                :disabled="!checkIn || !checkOut || guests < 1"
              >
                Search Rooms
              </button>
            </div>
          </div>
        </form>
        <div v-if="message" class="error-message">{{ message }}</div>
      </div>

      <!-- Step 2: Select Room -->
      <div v-if="currentStep === 2" class="step-card">
        <h2>Select Your Room(s)</h2>
        <div class="guest-info">
          <p>
            You need accommodation for
            <strong>{{ guests }} guest{{ guests > 1 ? "s" : "" }}</strong>
          </p>
          <p v-if="selectedRooms.length > 0" class="capacity-info">
            Selected rooms capacity:
            <strong>{{ totalSelectedCapacity }} guests</strong>
            <span
              v-if="totalSelectedCapacity < guests"
              class="capacity-warning"
            >
              (Need {{ guests - totalSelectedCapacity }} more guests capacity)
            </span>
          </p>
        </div>
        <div v-if="availableRooms.length" class="rooms-grid">
          <div
            v-for="room in availableRooms"
            :key="room.id"
            class="room-selection-card"
            :class="{
              selected: selectedRooms.some((r) => r.id === room.id),
              disabled: !canSelectRoom(room),
            }"
            @click="toggleRoomSelection(room)"
          >
            <div class="room-image-container">
              <img
                v-if="room.imageName"
                :src="`/room_img/${room.imageName}`"
                :alt="`${room.roomType} room`"
                class="room-image"
              />
              <div v-else class="room-image-placeholder">
                <span>🏨</span>
              </div>
            </div>
            <div class="room-info">
              <div class="room-title">Room #{{ room.roomNumber }}</div>
              <div class="room-details">{{ room.roomType }}</div>
              <div class="room-details" v-if="room.description">
                {{ room.description }}
              </div>
              <div class="room-capacity">{{ room.capacity }} guests</div>
            </div>
            <div class="room-booking">
              <div class="room-price">
                <div class="room-price-amount">${{ room.pricePerNight }}</div>
                <div class="room-price-period">per night</div>
              </div>
              <div class="select-indicator">
                <span v-if="selectedRooms.some((r) => r.id === room.id)"
                  >✓ Selected</span
                >
                <span v-else-if="!canSelectRoom(room)"
                  >Capacity: {{ room.capacity }}</span
                >
                <span v-else>Click to Select</span>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="no-rooms">
          <p>
            No rooms available for the selected dates. Please try different
            dates.
          </p>
        </div>
        <div class="step-actions">
          <button @click="currentStep = 1" class="btn-secondary">Back</button>
          <button
            @click="currentStep = 3"
            :disabled="!canProceedToContact"
            class="btn-primary"
          >
            Continue
          </button>
        </div>
      </div>

      <!-- Step 3: Contact Information -->
      <div v-if="currentStep === 3" class="step-card">
        <h2>Contact Information</h2>
        <div class="contact-layout">
          <div class="contact-form-section">
            <form @submit.prevent="proceedToConfirmation" class="contact-form">
              <div class="form-group">
                <label>Title</label>
                <select v-model="contactInfo.title" required>
                  <option value="">Select Title</option>
                  <option value="Mr">Mr</option>
                  <option value="Mrs">Mrs</option>
                  <option value="Miss">Miss</option>
                  <option value="Master">Master</option>
                  <option value="Dr">Dr</option>
                  <option value="Prof">Prof</option>
                </select>
              </div>
              <div class="form-group">
                <label>Full Name</label>
                <input
                  type="text"
                  v-model="contactInfo.name"
                  placeholder="Enter your full name"
                  required
                />
              </div>
              <div class="form-group">
                <label>Email Address</label>
                <input
                  type="email"
                  v-model="contactInfo.email"
                  placeholder="Enter your email address"
                  required
                />
              </div>
              <button type="submit" class="btn-primary">Proceed</button>
            </form>
          </div>

          <div class="booking-summary-section">
            <div class="booking-summary">
              <h3>Booking Summary</h3>
              <div class="summary-dates">
                <div class="summary-item">
                  <span class="label">Check-in:</span>
                  <span class="value">{{ formatDate(checkIn) }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Check-out:</span>
                  <span class="value">{{ formatDate(checkOut) }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Nights:</span>
                  <span class="value"
                    >{{ nightsCount }} night{{
                      nightsCount > 1 ? "s" : ""
                    }}</span
                  >
                </div>
              </div>

              <div class="summary-rooms" v-if="selectedRooms.length > 0">
                <div
                  v-for="(room, index) in selectedRooms"
                  :key="room.id"
                  class="summary-room"
                >
                  <div class="room-image-summary">
                    <img
                      v-if="room.imageName"
                      :src="`/room_img/${room.imageName}`"
                      :alt="`${room.roomType} room`"
                      class="summary-room-image"
                    />
                    <div v-else class="summary-room-placeholder">
                      <span>🏨</span>
                    </div>
                  </div>
                  <div class="room-details-summary">
                    <div class="room-name">{{ room.roomType }}</div>
                    <div class="room-number">Room #{{ room.roomNumber }}</div>
                    <div class="room-description">
                      {{ room.description }}
                    </div>
                    <div class="room-capacity">{{ room.capacity }} guests</div>
                  </div>
                </div>
              </div>

              <div class="summary-pricing">
                <div class="price-breakdown">
                  <div
                    v-for="(room, index) in selectedRooms"
                    :key="room.id"
                    class="room-pricing"
                  >
                    <div class="price-item">
                      <span class="label"
                        >Room {{ index + 1 }} (#{{ room.roomNumber }}):</span
                      >
                      <span class="value">${{ room.pricePerNight }}/night</span>
                    </div>
                  </div>
                  <div class="price-item">
                    <span class="label">Nights:</span>
                    <span class="value">{{ nightsCount }}</span>
                  </div>
                  <div class="price-item">
                    <span class="label">Subtotal:</span>
                    <span class="value">${{ subtotal }}</span>
                  </div>
                  <div class="price-item">
                    <span class="label">Tax (10%):</span>
                    <span class="value">${{ tax }}</span>
                  </div>
                  <div class="price-item total">
                    <span class="label">Total:</span>
                    <span class="value">${{ totalPrice }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="step-actions">
          <button @click="currentStep = 2" class="btn-secondary">Back</button>
        </div>
      </div>

      <!-- Step 4: Confirmation -->
      <div v-if="currentStep === 4" class="step-card">
        <h2>Booking Confirmation</h2>
        <div class="confirmation-content">
          <div class="confirmation-success">
            <div class="success-icon">✓</div>
            <h3>Booking Confirmed!</h3>
            <p>Your booking has been successfully created.</p>
          </div>

          <div class="confirmation-details">
            <h4>Booking Details</h4>
            <div class="detail-section">
              <h5>Guest Information</h5>
              <div class="detail-item">
                <span class="label">Name:</span>
                <span class="value"
                  >{{ contactInfo.title }} {{ contactInfo.name }}</span
                >
              </div>
              <div class="detail-item">
                <span class="label">Email:</span>
                <span class="value">{{ contactInfo.email }}</span>
              </div>
            </div>

            <div class="detail-section">
              <h5>Stay Details</h5>
              <div class="detail-item">
                <span class="label">Check-in:</span>
                <span class="value">{{ formatDate(checkIn) }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Check-out:</span>
                <span class="value">{{ formatDate(checkOut) }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Guests:</span>
                <span class="value">{{ guests }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Nights:</span>
                <span class="value">{{ nightsCount }}</span>
              </div>
            </div>

            <div class="detail-section" v-if="selectedRooms.length > 0">
              <h5>Room Details</h5>
              <div
                v-for="(room, index) in selectedRooms"
                :key="room.id"
                class="room-confirmation"
              >
                <img
                  v-if="room.imageName"
                  :src="`/room_img/${room.imageName}`"
                  :alt="`${room.roomType} room`"
                  class="confirmation-room-image"
                />
                <div class="room-confirmation-details">
                  <div class="room-name">{{ room.roomType }}</div>
                  <div class="room-number">Room #{{ room.roomNumber }}</div>
                  <div class="room-description">
                    {{ room.description }}
                  </div>
                </div>
              </div>
            </div>

            <div class="detail-section">
              <h5>Pricing</h5>
              <div
                v-for="(room, index) in selectedRooms"
                :key="room.id"
                class="price-item"
              >
                <span class="label"
                  >Room {{ index + 1 }} (#{{ room.roomNumber }}):</span
                >
                <span class="value">${{ room.pricePerNight }}/night</span>
              </div>
              <div class="price-item">
                <span class="label">Subtotal:</span>
                <span class="value">${{ subtotal }}</span>
              </div>
              <div class="price-item">
                <span class="label">Tax (10%):</span>
                <span class="value">${{ tax }}</span>
              </div>
              <div class="price-item total">
                <span class="label">Total Amount:</span>
                <span class="value">${{ totalPrice }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="step-actions">
          <button @click="goToDashboard" class="btn-primary">
            Go to Dashboard
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from "vue";
import api from "../utils/api";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();

// Stepper state
const currentStep = ref(1);
const steps = ref([
  { title: "Room Search" },
  { title: "Select Room" },
  { title: "Contact Info" },
  { title: "Confirmation" },
]);

// Booking data
const checkIn = ref<string>((route.query.checkIn as string) || "");
const checkOut = ref<string>((route.query.checkOut as string) || "");
const guests = ref<number>(Number(route.query.guests || 1));
const availableRooms = ref<any[]>([]);
const selectedRooms = ref<any[]>([]);
const message = ref("");

// Date validation
const minDate = ref(new Date().toISOString().split("T")[0]);
const minCheckOutDate = computed(() => {
  if (!checkIn.value) return minDate.value;
  const checkInDate = new Date(checkIn.value);
  const nextDay = new Date(checkInDate);
  nextDay.setDate(nextDay.getDate() + 1);
  return nextDay.toISOString().split("T")[0];
});

// Contact information
const contactInfo = ref({
  title: "",
  name: "",
  email: "",
});

// Computed properties
const nightsCount = computed(() => {
  if (!checkIn.value || !checkOut.value) return 0;
  const start = new Date(checkIn.value);
  const end = new Date(checkOut.value);
  const diffTime = end.getTime() - start.getTime();
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
});

const totalSelectedCapacity = computed(() => {
  return selectedRooms.value.reduce((total, room) => total + room.capacity, 0);
});

const canProceedToContact = computed(() => {
  return (
    selectedRooms.value.length > 0 &&
    totalSelectedCapacity.value >= guests.value
  );
});

const subtotal = computed(() => {
  if (selectedRooms.value.length === 0 || nightsCount.value === 0) return 0;
  const totalRoomPrice = selectedRooms.value.reduce(
    (total, room) => total + room.pricePerNight,
    0
  );
  return totalRoomPrice * nightsCount.value;
});

const tax = computed(() => {
  return Math.round(subtotal.value * 0.1 * 100) / 100;
});

const totalPrice = computed(() => {
  return subtotal.value + tax.value;
});

// Methods
function formatDate(dateString: string) {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleDateString("en-US", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
}

async function searchRooms() {
  message.value = "";

  if (!checkIn.value || !checkOut.value || guests.value < 1) {
    message.value = "Please fill in all required fields";
    return;
  }

  try {
    const { data } = await api.get("/rooms/available/search", {
      params: {
        checkInDate: checkIn.value,
        checkOutDate: checkOut.value,
      },
    });
    availableRooms.value = data;

    if (data.length === 0) {
      message.value = "No rooms available for the selected dates";
    } else {
      currentStep.value = 2;
    }
  } catch (e: any) {
    console.error("Error searching rooms:", e);
    message.value = "Failed to search rooms. Please try again.";
  }
}

function canSelectRoom(room: any) {
  // Room can be selected if it has capacity (allows both selecting and unselecting)
  return room.capacity > 0;
}

function toggleRoomSelection(room: any) {
  if (!canSelectRoom(room)) return;

  const index = selectedRooms.value.findIndex((r) => r.id === room.id);
  if (index > -1) {
    // Room is already selected, so unselect it
    selectedRooms.value.splice(index, 1);
  } else {
    // Room is not selected, so select it
    selectedRooms.value.push(room);
  }
}

async function proceedToConfirmation() {
  if (
    !contactInfo.value.title ||
    !contactInfo.value.name ||
    !contactInfo.value.email
  ) {
    message.value = "Please fill in all contact information fields";
    return;
  }

  currentStep.value = 4;
  await createBooking();
}

async function createBooking() {
  try {
    // Create bookings for each selected room
    const bookingPromises = selectedRooms.value.map((room) => {
      const bookingData = {
        roomId: room.id,
        checkInDate: checkIn.value,
        checkOutDate: checkOut.value,
        numberOfGuests: Math.min(guests.value, room.capacity), // Distribute guests across rooms
        contactInformation: {
          title: contactInfo.value.title,
          fullName: contactInfo.value.name,
          email: contactInfo.value.email,
          phone: null, // Optional phone field
        },
      };
      return api.post("/bookings", bookingData);
    });

    await Promise.all(bookingPromises);
    message.value = "";
  } catch (e: any) {
    console.error("Booking error:", e);

    if (e?.response?.status === 401) {
      message.value = "Please login to create a booking";
      router.push("/login");
    } else if (e?.response?.status === 403) {
      message.value =
        "Access denied. You don't have permission to create bookings.";
    } else if (e?.response?.status === 400) {
      message.value = e?.response?.data || "Invalid booking data";
    } else {
      message.value =
        e?.response?.data ||
        e?.message ||
        "Failed to create booking. Please try again.";
    }
  }
}

function goToDashboard() {
  router.push("/dashboard");
}

// Initialize with query parameters
onMounted(() => {
  if (route.query.roomId && checkIn.value && checkOut.value) {
    // If coming from room selection, go directly to step 2
    searchRooms().then(() => {
      if (availableRooms.value.length > 0) {
        const roomId = Number(route.query.roomId);
        const room = availableRooms.value.find((r: any) => r.id === roomId);
        if (room) {
          selectedRooms.value = [room];
          currentStep.value = 2;
        }
      }
    });
  }
});
</script>
