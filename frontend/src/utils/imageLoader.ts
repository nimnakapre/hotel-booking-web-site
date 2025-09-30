/**
 * Dynamic image loader utility using import.meta.glob
 * This provides better performance and bundling for dynamic image imports
 */

// Import all images from the room_img directory using import.meta.glob
const roomImages = import.meta.glob("/src/assets/room_img/*.jpg", {
  eager: true,
}) as Record<string, { default: string }>;

/**
 * Get the URL for a room image by filename
 * @param imageName - The filename of the image (e.g., 'deluxe-suite-101.jpg')
 * @returns The URL of the image or undefined if not found
 */
export function getRoomImageUrl(
  imageName: string | null | undefined
): string | undefined {
  if (!imageName) return undefined;

  // Ensure the image name has the correct extension
  const fileName = imageName.endsWith(".jpg") ? imageName : `${imageName}.jpg`;
  const imagePath = `/src/assets/room_img/${fileName}`;

  // Return the bundled URL if the image exists
  if (roomImages[imagePath]) {
    return roomImages[imagePath].default;
  }

  // Fallback to undefined if image not found
  console.warn(`Room image not found: ${fileName}`);
  return undefined;
}

/**
 * Get the URL for a room image by filename (for use in templates)
 * @param imageName - The filename of the image (e.g., 'deluxe-suite-101.jpg')
 * @returns The URL of the image or empty string if not found
 */
export function getRoomImageSrc(imageName: string | null | undefined): string {
  return getRoomImageUrl(imageName) || "";
}

/**
 * Get all available room image filenames
 * @returns Array of image filenames
 */
export function getAvailableRoomImages(): string[] {
  return Object.keys(roomImages).map((path) => {
    const fileName = path.split("/").pop() || "";
    return fileName.replace(".jpg", "");
  });
}
