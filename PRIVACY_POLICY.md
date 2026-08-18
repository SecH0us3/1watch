# Privacy Policy for 1watch

**Last updated: August 18, 2026**

This Privacy Policy describes how **1watch** ("we", "our", or "the app") handles your information. We are committed to protecting your privacy and ensuring transparency about what data is used and why.

---

### 1. Core Principle: Privacy by Design & Zero Telemetry
- **No Personal Data Collection**: We do not collect, store, transmit, or sell any personally identifiable information (such as your name, email, phone number, contacts, device identifiers, or IP addresses).
- **No User Accounts**: 1watch operates completely without accounts, registration, or logins.
- **No Trackers or Analytics**: There are no tracking SDKs, behavioral analytics, telemetry services, or third-party advertising frameworks in the application.

---

### 2. Permissions & Data Usage

#### Approximate Location (`ACCESS_COARSE_LOCATION`)
- **Purpose**: 1watch utilizes your approximate location strictly for two on-device astronomical and environmental calculations:
  1. **Solar Horizon Calculation**: To mathematically calculate local sunrise, solar noon, sunset, civil twilight, and polar day/night cycles on the 24-hour clock face.
  2. **UV Index Retrieval**: To query the open meteorological service for current and hourly UV radiation levels.
- **Processing**: Location coordinates are processed **entirely on your local device**. Your location is never sent to our servers or shared with any third party.
- **Optional**: You can decline this permission. If declined, 1watch functions with default astronomical calculations without requesting location again.

#### Wallpaper Permissions (`SET_WALLPAPER`, `BIND_WALLPAPER_SERVICE`)
- **Purpose**: Enables the rendering of the live wallpaper clock on your home screen and lock screen.
- **Processing**: The live wallpaper engine runs locally on your device's GPU/canvas.

---

### 3. Third-Party Services
- **Open-Meteo Weather API**: To provide accurate UV Index data, the app sends rounded approximate latitude and longitude coordinates to the public Open-Meteo API. No personal identifiers, device IDs, or user tokens are sent.

---

### 4. Data Storage & Security
- **Local Storage Only**: All user preferences (selected theme, bezel finish, font styles, custom background color) are stored locally on your device using Android's encrypted SharedPreferences.
- **Data Deletion**: Uninstalling the application immediately and permanently removes all stored preferences and cache.

---

### 5. Children's Privacy
1watch does not target or knowingly collect data from children under the age of 13. The app is completely safe for users of all ages.

---

### 6. Changes to This Privacy Policy
We may update our Privacy Policy from time to time. Any changes will be posted on this page with an updated revision date.

---

### 7. Contact Us
If you have any questions or suggestions regarding this Privacy Policy, please contact us at:
- **Email**: privacy@1watch.app
