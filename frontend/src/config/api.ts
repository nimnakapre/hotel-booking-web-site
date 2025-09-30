// API Configuration
export const API_CONFIG = {
  // Production API URL
  PRODUCTION_URL: "https://hoteldna.duckdns.org/api",
  // Development API URL
  DEVELOPMENT_URL: "https://hoteldna.duckdns.org/api",
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
