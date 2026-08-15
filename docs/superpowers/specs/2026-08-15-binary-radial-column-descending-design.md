# Binary Radial Column (Upright Glyphs Descending to Center) Design

## 1. Overview
When the user selects the `BINARY` numeral style (`00000`..`10111`), the 5 bits of each binary hour number must descend vertically/radially from the dial outer rim towards the dial center as a 5-digit radial column of upright glyphs (Option B).

---

## 2. Mathematical Geometry & Rendering Specification

### Angular and Radial Spoke Calculations
For each hour $h \in [0..23]$ with angle $\theta = \text{timeToAngle}(h) - 90^\circ$ and $\text{rad} = \text{radians}(\theta)$:
- Unit direction vector from center $(cx, cy)$ towards hour tick:
  $$u_x = \cos(\text{rad}), \quad u_y = \sin(\text{rad})$$

- Start radius (for bit 4, MSB, near outer tick):
  $$r_{\text{start}} = \text{radius} - \text{tickLength} - \text{baseSize} \cdot 0.8f$$

- Radial step between bits:
  $$\Delta r = \begin{cases} \text{radius} \cdot 0.040f & \text{if ALL numerals} \\ \text{radius} \cdot 0.046f & \text{if EVEN/ODD} \end{cases}$$

- For each bit index $i \in [0..4]$ in 5-bit binary string `labelText` (`labelText[0]` = MSB, `labelText[4]` = LSB):
  $$r_i = r_{\text{start}} - i \cdot \Delta r$$
  $$x_i = cx + r_i \cdot \cos(\text{rad})$$
  $$y_i = cy + r_i \cdot \sin(\text{rad})$$

### Glyph Presentation
- Each character `ch = labelText[i].toString()` is rendered strictly upright (`textAlign = Paint.Align.CENTER`) at $(x_i, y_i + \text{textVerticalOffset})$.
- No canvas rotation for individual glyphs under Option B, ensuring maximum legibility of 0s and 1s while perfectly organizing them as a radial ray spoke descending into the center.

---

## 3. Dual-Mask Day/Night Contrast
- The binary radial column rendering is executed identically within both the Day zone pass (clipped to `dayZonePath`) and Night zone pass (clipped to `nightZonePath`), guaranteeing full dual-mask contrast when rays cross the solar horizon line.

---

## 4. Verification & Testing
- Unit test in `Uno24DialRendererTest.kt` verifying binary string formatting for all 24 hours.
- Visual emulator verification with live screenshot on `emulator-5554` verifying radial column descent from tick to center across all hours.
