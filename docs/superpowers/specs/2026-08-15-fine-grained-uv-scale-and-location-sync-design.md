# Fine-Grained 12-Tier UV Color Scale & Active Location Sync Design

## 1. Overview
This feature introduces two key improvements:
1. **Fine-Grained 12-Tier UV Color Spectrum**: Expands `Uno24DialRenderer.getUvColor(uvVal)` from coarse 5 bands to 12 distinct 1.0-step color tiers ranging from Emerald Green (`#4CAF50`) at UV 1.0 through Lime, Chartreuse, Yellow, Amber, Tangerine, Flame Coral (`#FF5722`) at UV 8, Crimson, Magenta, Purple, and Deep UV Violet (`#6A1B9A`) at UV 12+.
2. **Active GPS / Network Location Sync & Open-Meteo Cache Keying**:
   - `LocationHelper.updateLocationIfPermitted(context, onLocationUpdated)`: actively requests single fresh location updates via `LocationManager.requestSingleUpdate` or GPS/Network providers if `getLastKnownLocation` is null or stale.
   - `UvRepository`: keys cached UV data by rounded latitude, longitude and date (`"uv_cache_${round(lat, 2)}_${round(lon, 2)}_${date}"`) and fires an invalidation callback when background network fetch succeeds to immediately redraw `clockView` and wallpaper service.

---

## 2. 12-Tier UV Color Table

| UV Range | Color Hex | Color Name | Int Value |
|---|---|---|---|
| `< 0.5` | `0x00000000` | Transparent (Night / No UV) | `0` |
| `0.5 .. 1.4` | `#4CAF50` | Emerald Green | `0xFF4CAF50.toInt()` |
| `1.5 .. 2.4` | `#8BC34A` | Light Lime | `0xFF8BC34A.toInt()` |
| `2.5 .. 3.4` | `#CDDC39` | Chartreuse Yellow-Green | `0xFFCDDC39.toInt()` |
| `3.5 .. 4.4` | `#FFEB3B` | Lemon Yellow | `0xFFFFEB3B.toInt()` |
| `4.5 .. 5.4` | `#FDD835` | Amber Gold | `0xFFFDD835.toInt()` |
| `5.5 .. 6.4` | `#FFB300` | Honey Amber | `0xFFFFB300.toInt()` |
| `6.5 .. 7.4` | `#FB8C00` | Tangerine Orange | `0xFFFB8C00.toInt()` |
| `7.5 .. 8.4` | `#FF5722` | Flame Coral (Paphos UV 8) | `0xFFFF5722.toInt()` |
| `8.5 .. 9.4` | `#E53935` | Crimson Red | `0xFFE53935.toInt()` |
| `9.5 .. 10.4`| `#D81B60` | Ruby Magenta | `0xFFD81B60.toInt()` |
| `10.5 .. 11.4`| `#9C27B0`| Amethyst Purple | `0xFF9C27B0.toInt()` |
| `11.5+` | `#6A1B9A` | Deep UV Violet | `0xFF6A1B9A.toInt()` |

---

## 3. Location Synchronization & Live UV Fetch
- When permissions are granted, `LocationHelper` registers a single listener for `LocationManager.GPS_PROVIDER` and `NETWORK_PROVIDER`.
- Once location updates, it saves `lat`/`lon` and invokes `onLocationUpdated()`.
- `UvRepository` fetches forecast from Open-Meteo for the updated coordinates asynchronously and triggers a listener callback to re-render the clock immediately with actual UV numbers.

---

## 4. Verification & Testing
- Unit tests in `Uno24DialRendererTest.kt` verifying all 12 color ranges.
- Unit tests in `UvRepositoryTest.kt` verifying cache keying and fallback calculation.
- Live verification on emulator.
