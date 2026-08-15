# Full-Spectrum WHO UV Color Index & 1watch App Renaming Design

## 1. Overview
This design covers two enhancements:
1. **Full-Spectrum WHO 5-Tier Color Coding for UV Activity Arc and Complication Dot**: Replaces the limited thresholding with authentic WHO Global Solar UV Index colors spanning the full daylight spectrum from morning green (0.5–2.9) to extreme violet (11+).
2. **App Renaming to 1watch**: Renames the application from "UNO 24" to "1watch" across all 12 localization resources and manifest descriptors.

---

## 2. WHO UV Index Color Scale Specification
The World Health Organization (WHO) and Global Solar UV Index define standard color bands:

| UV Index Range | Category | Color Hex | Visual Description |
|---|---|---|---|
| `< 0.5` | None / Night | Transparent | Not drawn |
| `0.5 .. 2.9` | Low (Низкий) | `#4CAF50` | Emerald Green |
| `3.0 .. 5.9` | Moderate (Умеренный) | `#FDD835` | Sunbeam Yellow |
| `6.0 .. 7.9` | High (Высокий) | `#FB8C00` | Bright Orange |
| `8.0 .. 10.9` | Very High (Очень высокий) | `#E53935` | Crimson Red |
| `11.0+` | Extreme (Экстремальный) | `#8E24AA` | Royal Purple / Violet |

### Rendering Details
- **UV Activity Arc**: Each daytime hour $h \in [0..23]$ with UV value $\ge 0.5$ draws an arc segment at radius $R_{uv} = 0.94 \cdot R$ with angle `timeToAngle(h.toDouble()) - 90f` and sweep `15f`, using `getUvColor(uvVal)`.
- **Complication Indicator**: When `showUvIndex = true` and `currentUv >= 0.5f`, the dot preceding `UV X.X` is painted with `getUvColor(currentUv)`.

---

## 3. App Renaming Specification
- Update `app_name` string in all 12 language resources:
  - `values/strings.xml`: `1watch`
  - `values-ru/strings.xml`: `1watch`
  - `values-es/strings.xml`: `1watch`
  - `values-de/strings.xml`: `1watch`
  - `values-fr/strings.xml`: `1watch`
  - `values-zh-rCN/strings.xml`: `1watch`
  - `values-ja/strings.xml`: `1watch`
  - `values-hi/strings.xml`: `1watch`
  - `values-la/strings.xml`: `1watch`
  - `values-el/strings.xml`: `1watch`
  - `values-b+ang/strings.xml`: `1watch`

---

## 4. Verification & Testing
- Unit tests in `Uno24DialRendererTest.kt` verifying `getUvColor` mapping for each range.
- Build test `./gradlew test assembleDebug`.
- Visual emulator verification with live screenshot on `emulator-5554`.
