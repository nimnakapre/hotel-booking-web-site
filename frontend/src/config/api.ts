// API Configuration
export const API_CONFIG = {
  // Production API URL
  PRODUCTION_URL: "http://13.60.211.47:8080/api",
  // Development API URL
  DEVELOPMENT_URL: "http://localhost:8080/api",
  // Get the appropriate URL based on environment
  getBaseURL: () => {
    // Check if we're running on localhost (development)
    if (
      typeof window !== "undefined" &&
      window.location.hostname === "localhost"
    ) {
      return API_CONFIG.DEVELOPMENT_URL;
    }
    // For all other cases (production builds), use production URL
    return API_CONFIG.PRODUCTION_URL;
  },
};

export default API_CONFIG;
