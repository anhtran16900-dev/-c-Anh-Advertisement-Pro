package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SignageRepository
import com.example.model.ProjectDossierItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Đức Anh Ad PRO", appName)
    }

    @Test
    fun `repository save and load project items`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = SignageRepository(context)

        repo.clearProject()
        assertEquals(0, repo.projectItems.value.size)

        val item = ProjectDossierItem(
            name = "Biển hiệu Showroom",
            categoryName = "Biển Bạt KTS",
            dimensions = "6m x 2.4m",
            quantity = 1,
            totalCost = 3500000.0,
            finalPrice = 4725000.0
        )
        repo.addProjectItem(item)
        assertEquals(1, repo.projectItems.value.size)
        assertEquals("Biển hiệu Showroom", repo.projectItems.value[0].name)
    }

    @Test
    fun `generate and export PDF quotation file`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val input = com.example.model.EstimationInput(
            itemName = "Biển Alu Chữ Nổi Showroom VinFast",
            clientName = "Công ty TNHH VinFast",
            width = 6.0,
            height = 2.4,
            quantity = 1
        )
        val result = com.example.calculator.SignageCalculator.calculate(input, com.example.data.DefaultData.defaultUnitPrices)

        val pdfFile = com.example.util.ProjectExportManager.generatePdfQuotation(context, input, result)
        assertNotNull(pdfFile)
        org.junit.Assert.assertTrue("PDF file must exist", pdfFile.exists())
        org.junit.Assert.assertTrue("PDF file size must be greater than 0", pdfFile.length() > 0)
        org.junit.Assert.assertTrue("PDF file must have .pdf extension", pdfFile.name.endsWith(".pdf"))
    }

    @Test
    fun `parse Gemini 3_8 Flash structured material suggestion JSON`() {
        val rawJson = """
            {
              "engineeringSummary": "Dùng Alu Alcorest ngoài trời kết hợp chữ nổi Mica LED 12V.",
              "constructionNotes": [
                "Gia cố khung sắt hộp mạ kẽm 25x25mm chịu gió.",
                "Chống thấm silicone viền chữ Mica."
              ],
              "suggestedMaterials": [
                {
                  "name": "Alu Alcorest PVDF 3mm ngoài trời",
                  "category": "Alu (Aluminum)",
                  "spec": "Khổ 1.22x2.44m, độ dày nhôm 0.21mm",
                  "unit": "Tấm",
                  "estimatedQuantity": 4.0,
                  "estimatedUnitPrice": 465000.0,
                  "reason": "Chịu nắng hướng Tây bền màu ngoài trời.",
                  "isPrimaryMaterial": true
                },
                {
                  "name": "Mica Đài Loan FS 3mm trắng sữa",
                  "category": "Mica",
                  "spec": "Khổ 1.22x2.44m xuyên sáng tốt",
                  "unit": "Tấm",
                  "estimatedQuantity": 2.0,
                  "estimatedUnitPrice": 680000.0,
                  "reason": "Tán sáng đều cho chữ nổi ban đêm.",
                  "isPrimaryMaterial": false
                }
              ]
            }
        """.trimIndent()

        val parsed = com.example.ai.GeminiMaterialSuggestionService.parseSuggestionResponseJson(rawJson)
        assertEquals("Dùng Alu Alcorest ngoài trời kết hợp chữ nổi Mica LED 12V.", parsed.engineeringSummary)
        assertEquals(2, parsed.constructionNotes.size)
        assertEquals(2, parsed.materials.size)
        assertEquals("Alu Alcorest PVDF 3mm ngoài trời", parsed.materials[0].name)
        org.junit.Assert.assertTrue(parsed.materials[0].isPrimaryMaterial)
        assertEquals(465000.0, parsed.materials[0].estimatedUnitPrice, 0.01)
    }

    @Test
    fun `repository persists academy step completion progress`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = SignageRepository(context)

        repo.resetGuideProgress("maintop")
        assertEquals(false, "maintop_1" in repo.completedGuideSteps.value)

        repo.toggleGuideStep("maintop", 1)
        org.junit.Assert.assertTrue("maintop_1" in repo.completedGuideSteps.value)

        // Verify persistence across repository reload
        val reloadedRepo = SignageRepository(context)
        org.junit.Assert.assertTrue("maintop_1" in reloadedRepo.completedGuideSteps.value)

        reloadedRepo.markGuideCompleted("maintop", listOf(1, 2, 3, 4, 5))
        org.junit.Assert.assertTrue("maintop_5" in reloadedRepo.completedGuideSteps.value)

        reloadedRepo.resetGuideProgress("maintop")
        assertEquals(false, "maintop_1" in reloadedRepo.completedGuideSteps.value)
        assertEquals(false, "maintop_5" in reloadedRepo.completedGuideSteps.value)
    }

    @Test
    fun `repository updates dossier items and persists quote settings`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = SignageRepository(context)
        repo.clearProject()

        val original = ProjectDossierItem(
            id = 9991L,
            name = "Biển Mặt Tiền Cũ",
            categoryName = "Biển Alu",
            dimensions = "4.0m x 1.2m",
            quantity = 1,
            totalCost = 3000000.0,
            finalPrice = 4200000.0
        )
        repo.addProjectItem(original)
        repo.updateProjectItem(original.copy(name = "Biển Alu Inox LED Đã Sửa", finalPrice = 5500000.0, quantity = 2))

        val reloaded = SignageRepository(context)
        assertEquals(1, reloaded.projectItems.value.size)
        assertEquals("Biển Alu Inox LED Đã Sửa", reloaded.projectItems.value[0].name)
        assertEquals(5500000.0, reloaded.projectItems.value[0].finalPrice, 0.01)
        assertEquals(2, reloaded.projectItems.value[0].quantity)

        reloaded.saveDossierQuoteSettings(
            com.example.model.DossierQuoteSettings(
                companyName = "Xưởng Quảng Cáo Test",
                clientName = "Anh Minh",
                discountPercent = 5.0,
                vatPercent = 8.0,
                warrantyMonths = 36
            )
        )
        val reloadedSettings = SignageRepository(context).dossierQuoteSettings.value
        assertEquals("Xưởng Quảng Cáo Test", reloadedSettings.companyName)
        assertEquals("Anh Minh", reloadedSettings.clientName)
        assertEquals(5.0, reloadedSettings.discountPercent, 0.01)
        assertEquals(36, reloadedSettings.warrantyMonths)
    }

    @Test
    fun `parse 3d design JSON and generate 3d quotation PDF`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val rawJson = """
            {
              "conceptTitle": "Mặt Tiền Luxury Gold LED",
              "facadeAnalysis": "Cân đối mặt tiền 5m, chữ nổi đặt tại 65% chiều cao.",
              "visual3dSpec": "Nền Alu đen nhám + chữ Inox vàng gương dày 5cm.",
              "lightingPlan": "LED 12V hắt chân 3000K + Timer KG316T.",
              "installationMethod": "Khung sắt hộp 25x25mm mạ kẽm gia cố bản mã.",
              "estimatedDays": 4,
              "renderPromptEn": "Photorealistic 3D luxury gold storefront sign",
              "suggestedLines": [
                {
                  "name": "Mặt dựng Alu Alcorest 3mm",
                  "spec": "Khung sắt 25x25 + Alu đen nhám",
                  "unit": "m²",
                  "quantity": 9.0,
                  "unitPrice": 680000.0
                },
                {
                  "name": "Chữ nổi Inox Vàng + LED",
                  "spec": "Cắt Laser + LED 12V hắt chân",
                  "unit": "Bộ",
                  "quantity": 1.0,
                  "unitPrice": 4200000.0
                }
              ]
            }
        """.trimIndent()

        val spec = com.example.ai.GeminiDesign3dService.parseDesign3dSpecJson(rawJson)
        assertEquals("Mặt Tiền Luxury Gold LED", spec.conceptTitle)
        assertEquals(4, spec.estimatedDays)
        assertEquals(2, spec.suggestedLines.size)

        val config = com.example.model.Design3dPromptConfig(brandText = "GOLDEN COFFEE", widthM = 5.0, heightM = 1.8)
        val terms = com.example.model.Design3dQuoteTerms(clientName = "Chị Lan")
        val summary = com.example.model.Design3dQuoteSummary.calculate(spec.suggestedLines, terms)
        val sampleBmp = android.graphics.Bitmap.createBitmap(160, 90, android.graphics.Bitmap.Config.ARGB_8888)

        val pdfFile = com.example.util.Design3dPdfExporter.generate3dQuotePdf(
            context = context,
            config = config,
            spec = spec,
            mockupBitmap = sampleBmp,
            lines = spec.suggestedLines,
            terms = terms,
            summary = summary
        )
        org.junit.Assert.assertTrue("3D PDF must exist", pdfFile.exists())
        org.junit.Assert.assertTrue("3D PDF must be non-empty", pdfFile.length() > 0)
    }

    @Test
    fun `generate quotation PDFs for all 3 board families`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        com.example.model.BoardDesignFamily.entries.forEach { family ->
            val spec = com.example.model.BoardDesignSpec(
                family = family,
                brandText = "ĐỨC ANH PRO - ${family.shortTitle}",
                widthMeters = 4.5,
                heightMeters = 1.4,
                quantity = 1
            )
            val calc = com.example.calculator.SignageBoardDesignerCalculator.calculate(spec)
            val pdfFile = com.example.util.BoardQuotePdfExporter.generateBoardQuotePdf(context, spec, calc)
            org.junit.Assert.assertTrue("PDF for ${family.name} must exist", pdfFile.exists())
            org.junit.Assert.assertTrue("PDF for ${family.name} must be non-empty", pdfFile.length() > 0)

            val zalo = com.example.util.BoardQuotePdfExporter.buildBoardZaloQuote(spec, calc)
            org.junit.Assert.assertTrue(zalo.contains(family.fullTitle.uppercase(java.util.Locale.getDefault())))
        }
    }

    @Test
    fun `repository persists custom categories item presets and install locations`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = SignageRepository(context)
        repo.resetDefaultCategories()

        val customCat = com.example.model.EditableCategoryOption(
            id = "custom_neon_flex",
            shortName = "Đèn Neon Sign LED",
            displayName = "MỤC 6: Uốn Chữ Neon LED Silicon Dẻo",
            baseCategory = com.example.model.SignageCategory.CNC_LASER,
            isBuiltIn = false
        )
        repo.addOrUpdateCategoryOption(customCat)
        repo.addItemPreset("custom_neon_flex", com.example.model.SignageCategory.CNC_LASER, "Chữ Neon Open 1.2m")
        repo.updateItemPreset("custom_neon_flex", com.example.model.SignageCategory.CNC_LASER, "Chữ Neon Open 1.2m", "Chữ Neon Quán Cafe 1.5m")

        val customLoc = com.example.model.InstallLocationPreset(
            id = "custom_loc_rooftop",
            name = "Sân Thượng Tầng 5 - Khung Thép",
            suggestedComplexity = com.example.model.LaborComplexity.HARD,
            isBuiltIn = false
        )
        repo.addOrUpdateInstallLocation(customLoc)

        val reloaded = SignageRepository(context)
        val loadedCat = reloaded.categoryOptions.value.firstOrNull { it.id == "custom_neon_flex" }
        assertNotNull(loadedCat)
        assertEquals("Đèn Neon Sign LED", loadedCat?.shortName)

        val presets = reloaded.getItemPresets("custom_neon_flex", com.example.model.SignageCategory.CNC_LASER)
        org.junit.Assert.assertTrue(presets.contains("Chữ Neon Quán Cafe 1.5m"))

        val loadedLoc = reloaded.installLocations.value.firstOrNull { it.id == "custom_loc_rooftop" }
        assertNotNull(loadedLoc)
        assertEquals("Sân Thượng Tầng 5 - Khung Thép", loadedLoc?.name)
        assertEquals(com.example.model.LaborComplexity.HARD, loadedLoc?.suggestedComplexity)
    }
}

