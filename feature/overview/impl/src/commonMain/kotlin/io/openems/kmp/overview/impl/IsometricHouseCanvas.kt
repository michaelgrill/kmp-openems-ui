package io.openems.kmp.overview.impl

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.cos
import kotlin.math.sin

// IsometricHouseCompose.kt
// Jetpack Compose Canvas drawing of an isometric house (outlines only)
// Includes: PV on roof, Battery storage, EV charging station + cable and car, small garden
// Wires connect PV -> Battery and PV -> EV charger


/**
 * Draws an isometric outline scene: house with PV on roof, battery storage, EV charger + car,
 * garden and connecting wires. Designed for stroke-only (no fills).
 *
 * Usage: place IsometricHouseCanvas() inside your Compose setContent.
 */

@Composable
fun IsometricHouseCanvas(
    modifier: Modifier = Modifier.fillMaxSize(),
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    // Canvas draws in available space; we translate + scale to center the isometric composition.
    Canvas(modifier = modifier.size(width = 24.dp, height = 12.dp)) {
        // viewport size
        val w = size.width
        val h = size.height

        // Isometric basis (20 degrees)
        val angle = (20.0 * kotlin.math.PI / 180f)
        val cx = cos(angle).toFloat() // cos30 = sqrt(3)/2
        val sx = sin(angle).toFloat() // sin30 = 1/2

        // Basis vectors for mapping 3D (x,y,z) -> 2D
        val ex = Offset(cx, -sx)     // X direction
        val ey = Offset(-cx, -sx)    // Y direction
        val ez = Offset(
            0f,
            -1f
        )      // Z (up) direction in screen coords — positive Z moves upward on screen

        // Scale and center
        val sceneSize = 400f // arbitrary scene size in iso units
        val scale = (minOf(w, h) * 0.75f) / sceneSize
        val origin = Offset(w / 2f, h * 0.52f) // place origin slightly below center

        fun iso(x: Float, y: Float, z: Float = 0f): Offset {
            // Combine basis vectors, then scale and translate to origin
            val px = ex.x * x + ey.x * y + ez.x * z
            val py = ex.y * x + ey.y * y + ez.y * z
            return Offset(origin.x + px * scale, origin.y + py * scale)
        }

        // Stroke paint
        val stroke = Stroke(width = 3f, miter = 4f)

        // ---------- House geometry (a simple rectangular prism + pitched roof) ----------
        // House footprint in iso units: we use x,y in plane and z as height
        val houseX = 70f
        val houseY = 70f
        val houseH = 50f

        // corners of base (z=0)
        val p000 = iso(0f, 0f, 0f)
        val pX00 = iso(houseX, 0f, 0f)
        val p0Y0 = iso(0f, houseY, 0f)
        val pXY0 = iso(houseX, houseY, 0f)

        // top corners (z=houseH)
        val p001 = iso(0f, 0f, houseH)
        val pX01 = iso(houseX, 0f, houseH)
        val p0Y1 = iso(0f, houseY, houseH)
        val pXY1 = iso(houseX, houseY, houseH)

        // roof ridge: we'll place ridge along X axis at mid Y
        val ridgeZ = houseH + 30f
        val pR0 = iso(0f, houseY / 2f, ridgeZ)
        val pRX = iso(houseX, houseY / 2f, ridgeZ)

        // Walls: draw base edges
        drawLine(
            color = color,
            start = p000,
            end = pX00,
            strokeWidth = stroke.width,
            cap = stroke.cap
        )
        drawLine(
            color = color,
            start = p000,
            end = p0Y0,
            strokeWidth = stroke.width,
            cap = stroke.cap
        )
//        drawLine(
//            color = Color.Black,
//            start = pX00,
//            end = pXY0,
//            strokeWidth = stroke.width,
//            cap = stroke.cap
//        )
//        drawLine(
//            color = Color.Black,
//            start = p0Y0,
//            end = pXY0,
//            strokeWidth = stroke.width,
//            cap = stroke.cap
//        )

        // Vertical edges
        drawLine(color = color, start = p000, end = p001, strokeWidth = stroke.width)
        drawLine(
            color = color,
            start = pX00,
            end = pX01.minus(Offset(0f, -17f)),
            strokeWidth = stroke.width
        )
        drawLine(color = color, start = p0Y0, end = p0Y1, strokeWidth = stroke.width)
//        drawLine(color = Color.Black, start = pXY0, end = pXY1, strokeWidth = stroke.width)

        // Top edges (house top rectangle)
        drawLine(
            color = color,
            start = p001.minus(Offset(-10f, -15f)),
            end = pX01.minus(Offset(-10f, -15f)),
            strokeWidth = stroke.width
        )
//        drawLine(color = Color.Black, start = p001, end = p0Y1, strokeWidth = stroke.width)
//        drawLine(color = Color.Black, start = pX01, end = pXY1, strokeWidth = stroke.width)
//        drawLine(color = Color.Black, start = p0Y1, end = pXY1, strokeWidth = stroke.width)

        // Roof - two pitched faces between ridge and top edges
        // Left roof face (towards +Y)
        drawLine(
            color = color,
            start = pR0,
            end = p001.minus(Offset(-10f, -15f)),
            strokeWidth = stroke.width
        )
        drawLine(
            color = color,
            start = pR0,
            end = p0Y1.minus(Offset(15f, -10f)),
            strokeWidth = stroke.width
        )
//        drawLine(color = Color.Black, start = pR0, end = pX01, strokeWidth = stroke.width)
        // Right ridge
        drawLine(
            color = color,
            start = pRX,
            end = pX01.minus(Offset(-10f, -15f)),
            strokeWidth = stroke.width
        )
//        drawLine(color = Color.Black, start = pRX, end = pXY1, strokeWidth = stroke.width)
//        drawLine(color = Color.Black, start = pRX, end = p001, strokeWidth = stroke.width)
        // ridge line
        drawLine(color = color, start = pR0, end = pRX, strokeWidth = stroke.width)

        // (Removed PV panels, battery, EV charger, garden — drawing only the house)(pos: Offset, text: String) {
        // draw small tag box (outline) — we don't draw text to keep "outlines only" request
//        val boxW = 36f
//        val boxH = 18f
//        val tl = Offset(pos.x - boxW / 2f, pos.y - boxH - 6f)
//        val tr = Offset(tl.x + boxW, tl.y)
//        val bl = Offset(tl.x, tl.y + boxH)
//        val br = Offset(tr.x, tr.y + boxH)
//        drawLine(Color.Black, tl, tr, strokeWidth = stroke.width * 0.6f)
//        drawLine(Color.Black, tl, bl, strokeWidth = stroke.width * 0.6f)
//        drawLine(Color.Black, bl, br, strokeWidth = stroke.width * 0.6f)
//        drawLine(Color.Black, tr, br, strokeWidth = stroke.width * 0.6f)
    }
}


@Preview
@Composable
fun PreviewIsometricHouse() {
    Surface(Modifier.fillMaxSize()) {
        IsometricHouseCanvas(modifier = Modifier.fillMaxSize())
    }
}
