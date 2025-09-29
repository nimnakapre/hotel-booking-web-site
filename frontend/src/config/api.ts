// API Configuration
export const API_CONFIG = {
  // Production API URL
  PRODUCTION_URL: "http://13.60.211.47:8080/api",
  // Development API URL
  DEVELOPMENT_URL: "http://localhost:8080/api",
  // Get the appropriate URL based on environment
  getBaseURL: () => {
    // Check if we're in development mode
    if (import.meta.env.DEV) {
      return API_CONFIG.DEVELOPMENT_URL;
    }
    // Use production URL for build
    return API_CONFIG.PRODUCTION_URL;
  },
};

export default API_CONFIG;
