# Design Spec: Luxury & Ethnic Dials for Uno24

**Date**: 2026-08-15  
**Topic**: Luxury Themes & Ethnic Numeral Systems for Uno24 Watchface & Live Wallpaper  

---

## 1. Overview
Enhance Uno24 with 5 authentic ethnic numeral systems and 5 luxury watchmaker themes, providing rich cultural typography and high-end horological aesthetics.

---

## 2. New Numeral Systems (`NumeralStyle`)

| Enum Name | Display Range | Example (00, 06, 12, 18, 23) | Description |
|---|---|---|---|
| `KANJI` | `〇`..`二十三` | `〇`, `六`, `十二`, `十八`, `二十三` | Traditional East Asian Kanji / Hanzi numerals |
| `DEVANAGARI` | `००`..`२३` | `००`, `०६`, `१२`, `१८`, `२३` | Sacred Indian Sanskrit & Hindi Devanagari script |
| `EASTERN_ARABIC` | `٠٠`..`٢٣` | `٠٠`, `٠٦`, `١٢`, `١٨`, `٢٣` | Eastern Arabic-Indic numerals / Arabic calligraphy |
| `GREEK` | `Αʹ`..`ΚΔʹ` | `ΚΔʹ`, `Ϛʹ`, `ΙΒʹ`, `ΙΗʹ`, `ΚΓʹ` | Classical Ancient Greek alphabetic numerals (Ionic / Milesian) |
| `HEBREW` | `א׳`..`כ״ד` | `כ״ד`, `ו׳`, `י״ב`, `י״ח`, `כ״ג` | Traditional Hebrew Gematria alphabetic numerals |

### Sizing and Radial Scaling
- In `Uno24DialRenderer`:
  - `BINARY`: Binary digits are strictly rendered radially directed towards the center (`numeralOrientation == NumeralOrientation.RADIAL || numeralStyle == NumeralStyle.BINARY`) so 5 digits (`00000`..`10111`) fit seamlessly without horizontal overlaps.
  - `KANJI`: Scaled appropriately for 1 to 3 characters (`二十三`).
  - `DEVANAGARI` & `EASTERN_ARABIC`: Scaled similarly to Arabic 2-digit pairs.
  - `GREEK` & `HEBREW`: Scaled appropriately for 2 to 3 glyphs with gershayim / keraia punctuation.

---

## 3. New Luxury Themes (`DialTheme`)

| Enum Name | Day Sky / Background | Night Sky / Background | Hand & Center Color | Accents & Text | Inspiration |
|---|---|---|---|---|---|
| `ROSE_GOLD_ONYX` | `#EFEBE9` (Pearl warm grey) | `#181514` (Deep onyx) | `#E0A899` (Rose Gold) | `#3E2723` / `#E0A899` | Swiss haute horlogerie in 18k Rose Gold |
| `ROYAL_EMERALD` | `#E8F5E9` (Mint pearl) | `#0B1E13` (Imperial Emerald) | `#F3C644` (Imperial Gold) | `#1B4332` / `#F3C644` | High-jewellery emerald dials & gold bezels |
| `URUSHI_JAPAN` | `#F8F3E6` (Japanese parchment) | `#121214` (Obsidian Urushi lacquer) | `#E63946` (Cinnabar Vermilion 朱色) | `#1D1E2C` / `#E5B25D` (Maki-e Gold) | Traditional Japanese lacquerware & cinnabar |
| `ARABIAN_LAPIS` | `#EDF2F7` (Desert pearl) | `#0B192C` (Royal Lapis Lazuli) | `#FFD700` (Pure Gold) | `#1E3E62` / `#FFD700` | Celestial Middle Eastern lapis lazuli & astronomical astrolabes |
| `NORDIC_PLATINUM` | `#F1F3F5` (Frost white) | `#1E2022` (Nordic titanium) | `#E2E8F0` (Liquid Platinum) | `#334155` / `#E2E8F0` | Minimalist Scandinavian platinum & architectural titanium |

---

## 4. UI & Localization Integration

1. **`MainActivity.kt`**:
   - `spinnerNumeralStyle`: Populate with 10 total numeral systems (Arabic, Roman, Hexadecimal, Binary, Octal, Kanji, Devanagari, Eastern Arabic, Greek, Hebrew).
   - `spinnerTheme`: Populate with 14 total luxury themes (6 light, 3 dark/cyber, 5 luxury/ethnic).
2. **Localization**:
   - Add localized string keys to all 12 `strings.xml` files (`values/`, `values-ru/`, `values-es/`, `values-de/`, `values-fr/`, `values-zh-rCN/`, `values-ja/`, `values-hi/`, `values-la/`, `values-el/`, `values-b+ang/`).

---

## 5. Testing & Verification
- Unit tests in `Uno24DialRendererTest.kt` verifying all 10 `NumeralStyle` values and formatting outputs across 0..23 hours.
- Unit tests verifying all 14 `DialTheme` values.
- On-device screenshot testing on Android emulator for visual perfection of glyphs and color palettes.
