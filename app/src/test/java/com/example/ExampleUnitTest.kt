package com.example

import com.example.calculator.SignageCalculator
import com.example.data.DefaultData
import com.example.model.EstimationInput
import com.example.model.LaborComplexity
import com.example.model.SignageCategory
import com.example.util.Formatters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testHiflexCalculation() {
        val input = EstimationInput(
            category = SignageCategory.HIFLEX,
            itemName = "Biển Bạt In UV",
            width = 6.0,
            height = 2.4,
            quantity = 1,
            wastagePercent = 8.0,
            laborComplexity = LaborComplexity.MEDIUM,
            profitMarginPercent = 35.0,
            shippingAndHardwareFee = 350000.0,
            hiflexMaterialKey = "bat_khonggan_uv_m2"
        )
        val result = SignageCalculator.calculate(input, DefaultData.defaultUnitPrices)

        assertEquals(14.4, result.netArea, 0.01)
        assertEquals(14.4 * 1.08, result.grossAreaWithWastage, 0.01)
        assertTrue(result.breakdownItems.isNotEmpty())
        assertTrue(result.totalCost > 0)
        assertTrue(result.profitAmount > 0)
        assertTrue(result.finalCustomerPrice > result.totalCost)
        assertTrue(result.unitPricePerM2 > 0)
    }

    @Test
    fun testAluLetterCalculation() {
        val input = EstimationInput(
            category = SignageCategory.ALU_LETTER,
            itemName = "Biển Alu Chữ Inox",
            width = 5.0,
            height = 3.0,
            quantity = 1,
            wastagePercent = 10.0,
            laborComplexity = LaborComplexity.HARD,
            profitMarginPercent = 40.0,
            shippingAndHardwareFee = 500000.0,
            aluBrandKey = "alu_alcorest_pet_360k",
            letterMaterialKey = "inox_304_gold_m2",
            ledBrandKey = "led_module_samsung"
        )
        val result = SignageCalculator.calculate(input, DefaultData.defaultUnitPrices)

        assertEquals(15.0, result.netArea, 0.01)
        assertTrue(result.breakdownItems.any { it.name.contains("Tấm Alu") })
        assertTrue(result.breakdownItems.any { it.name.contains("Khung sắt") })
        assertTrue(result.breakdownItems.any { it.name.contains("Inox 304") })
        assertTrue(result.breakdownItems.any { it.name.contains("LED") })
        assertTrue(result.aiEngineeringTips.isNotEmpty())
    }

    @Test
    fun testProjectDetailsAndFormInputs() {
        val input = EstimationInput(
            itemName = "Mặt dựng Showroom VinFast",
            clientName = "Tập Đoàn Vingroup",
            projectLocation = "Phạm Văn Đồng, Cầu Giấy, Hà Nội",
            installationDate = "25/11/2026",
            projectNotes = "Lắp đặt ban đêm sau 22h, cần xe cẩu 5 tấn",
            category = SignageCategory.ALU_LETTER,
            width = 8.0,
            height = 3.5,
            quantity = 2
        )
        assertEquals("Mặt dựng Showroom VinFast", input.itemName)
        assertEquals("Tập Đoàn Vingroup", input.clientName)
        assertEquals("Phạm Văn Đồng, Cầu Giấy, Hà Nội", input.projectLocation)
        assertEquals("25/11/2026", input.installationDate)
        assertEquals("Lắp đặt ban đêm sau 22h, cần xe cẩu 5 tấn", input.projectNotes)

        val result = SignageCalculator.calculate(input, DefaultData.defaultUnitPrices)
        assertEquals(56.0, result.netArea, 0.01)
        assertTrue(result.finalCustomerPrice > 0)
    }

    @Test
    fun testFormatters() {
        val formatted = Formatters.formatVnd(1500000.0)
        assertTrue(formatted.contains("1.500.000") || formatted.contains("1,500,000"))
        assertEquals("15,00 m²", Formatters.formatArea(15.0).replace(".", ","))
    }

    @Test
    fun testInstantAiResponses() {
        val aluResponse = DefaultData.getInstantAiResponse("bóc tách biển alu 15m2 chữ nổi")
        assertTrue(aluResponse.contains("BÓC TÁCH VẬT TƯ BIỂN ALU"))

        val b3mResponse = DefaultData.getInstantAiResponse("so sánh bạt 3M")
        assertTrue(b3mResponse.contains("Bạt 3M Panagraphics"))

        val printerResponse = DefaultData.getInstantAiResponse("sửa lỗi máy in phun bị sọc pass")
        assertTrue(printerResponse.contains("Nozzle Check"))
    }

    @Test
    fun testAdvertisingMaterialsAndChecklistSynthesis() {
        val materials = DefaultData.defaultMaterials

        // Verify Mica, Aluminum, and PVC / Formex are in the catalog
        val hasMica = materials.any { it.category.contains("Mica", ignoreCase = true) }
        val hasAlu = materials.any { it.category.contains("Alu", ignoreCase = true) }
        val hasPvc = materials.any { it.category.contains("PVC", ignoreCase = true) || it.category.contains("Formex", ignoreCase = true) }

        assertTrue("Catalog must contain Mica materials", hasMica)
        assertTrue("Catalog must contain Aluminum/Alu materials", hasAlu)
        assertTrue("Catalog must contain PVC/Formex materials", hasPvc)

        val input = EstimationInput(
            category = SignageCategory.ALU_LETTER,
            itemName = "Biển hiệu Showroom Alu Chữ Mica LED",
            width = 6.0,
            height = 2.4,
            quantity = 1,
            selectedMaterialKey = "alu_alcorest_pet_360k",
            selectedMaterialName = "Alu Alcorest PET 3mm",
            selectedMaterialUnit = "Tấm",
            includeIronFrame = true,
            includeLighting = true,
            includeEdgeTrim = true,
            includeTimerRelay = true
        )

        val result = SignageCalculator.calculate(input, DefaultData.defaultUnitPrices)

        assertNotNull(result.advertisingChecklist)
        assertTrue("Checklist should not be empty", result.advertisingChecklist.isNotEmpty())

        val categories = result.advertisingChecklist.map { it.category }.toSet()
        assertTrue(categories.any { it.contains("Phôi Mặt Biển") })
        assertTrue(categories.any { it.contains("Khung Xương") })
        assertTrue(categories.any { it.contains("Chiếu Sáng") })
        assertTrue(categories.any { it.contains("Hoàn Thiện") })
        assertTrue(categories.any { it.contains("Nhân Công") })
    }

    @Test
    fun testAutomaticEstimatedTotalPriceFunction() {
        // Test 1: Simple 3m x 2m banner with unit price 120,000 VND / m²
        val total1 = SignageCalculator.calculateEstimatedTotalPrice(
            length = 3.0,
            width = 2.0,
            materialUnitPrice = 120000.0,
            unit = "m²",
            quantity = 1,
            wastagePercent = 0.0,
            profitMarginPercent = 0.0,
            includeIronFrame = false,
            includeLighting = false
        )
        // Area = 6.0 m². Material = 720,000. Labor = 60,000 * 6 = 360,000. Shipping = 200,000. Total cost = 1,280,000.
        assertTrue("Total price must exceed material cost", total1 >= 720000.0)

        // Test 2: Dimensions 6m x 2.4m, unit price 360,000 VND/Tấm (sheet 2.9768 m²), with iron frame and lighting
        val total2 = SignageCalculator.calculateEstimatedTotalPrice(
            length = 6.0,
            width = 2.4,
            materialUnitPrice = 360000.0,
            unit = "Tấm",
            quantity = 2,
            wastagePercent = 10.0,
            profitMarginPercent = 35.0,
            includeIronFrame = true,
            includeLighting = true
        )
        assertTrue("Total price for large sign must be positive and substantial", total2 > 10000000.0)

        // Test 3: Zero or invalid dimensions return safe minimum
        val totalZero = SignageCalculator.calculateEstimatedTotalPrice(
            length = 0.0,
            width = 0.0,
            materialUnitPrice = 350000.0,
            unit = "m²"
        )
        assertEquals(0.0, totalZero, 0.01)
    }

    @Test
    fun testExportManagerFileGenerators() {
        val input = EstimationInput(
            itemName = "Biển Hiệu Đại Lý Honda",
            clientName = "Nguyễn Văn Toàn",
            projectLocation = "123 Cầu Giấy, Hà Nội",
            installationDate = "30/11/2026",
            projectNotes = "Gia cố cột chịu lực",
            category = SignageCategory.ALU_LETTER,
            width = 5.0,
            height = 2.0,
            quantity = 1,
            wastagePercent = 5.0,
            profitMarginPercent = 30.0,
            selectedMaterialName = "Alu Alcorest 3mm",
            selectedMaterialUnit = "Tấm"
        )
        val result = SignageCalculator.calculate(input, DefaultData.defaultUnitPrices)

        // 1. HTML Quotation test
        val html = com.example.util.ProjectExportManager.generateHtmlQuotation(input, result)
        assertNotNull(html)
        assertTrue("HTML should contain DOCTYPE", html.contains("<!DOCTYPE html>"))
        assertTrue("HTML should contain company name", html.contains("ĐỨC ANH AD PRO"))
        assertTrue("HTML should contain item name", html.contains("Biển Hiệu Đại Lý Honda"))
        assertTrue("HTML should contain client name", html.contains("Nguyễn Văn Toàn"))
        assertTrue("HTML should contain table data", html.contains("<table class=\"data-table\">"))

        // 2. CSV Materials Breakdown test
        val csv = com.example.util.ProjectExportManager.generateCsvBreakdown(input, result)
        assertNotNull(csv)
        assertTrue("CSV must begin with UTF-8 BOM for Microsoft Excel compatibility", csv.startsWith("\uFEFF"))
        assertTrue("CSV must contain project title", csv.contains("Biển Hiệu Đại Lý Honda"))
        assertTrue("CSV must contain STT header", csv.contains("STT,Hạng Mục / Chủng Loại Vật Tư,Đơn Vị Tính"))
        assertTrue("CSV must contain total cost", csv.contains("TỔNG GIÁ BÁO CHO KHÁCH HÀNG"))

        // 3. Checklist CSV test
        val checklistCsv = com.example.util.ProjectExportManager.generateChecklistCsv(result)
        assertNotNull(checklistCsv)
        assertTrue("Checklist CSV must begin with UTF-8 BOM", checklistCsv.startsWith("\uFEFF"))
        assertTrue("Checklist CSV must contain column headers", checklistCsv.contains("STT,Phân Hệ Hạng Mục,Tên Vật Tư / Thiết Bị"))
        assertTrue("Checklist CSV must include items", checklistCsv.lines().size > 5)

        // 4. Zalo Text Quote test
        val zaloText = com.example.util.ProjectExportManager.generateZaloTextQuote(input, result)
        assertNotNull(zaloText)
        assertTrue("Zalo text should have company branding", zaloText.contains("ĐỨC ANH AD PRO"))
        assertTrue("Zalo text should have dimensions", zaloText.contains("5.0m x 2.0m"))
        assertTrue("Zalo text should have total price", zaloText.contains("TỔNG GIÁ TRỌN GÓI"))
    }

    @Test
    fun testGeminiMaterialPromptBuilder() {
        val input = EstimationInput(
            category = SignageCategory.ALU_LETTER,
            itemName = "Biển mặt tiền Showroom Cafe",
            width = 5.0,
            height = 2.0,
            quantity = 1,
            wastagePercent = 8.0,
            clientName = "Chuỗi Cafe Đen Đá",
            projectLocation = "Quận 1, TP.HCM",
            projectNotes = "Mặt tiền hướng Tây, cần chống nắng tốt",
            includeIronFrame = true,
            includeLighting = true,
            includeEdgeTrim = true,
            includeTimerRelay = true
        )
        val adDescription = "Biển quán Cafe hiện đại ngoài trời sáng ban đêm, chữ nổi mica hắt sáng"

        val prompt = com.example.ai.GeminiMaterialSuggestionService.buildMaterialSuggestionPrompt(
            input = input,
            advertisingDescription = adDescription,
            existingCatalog = DefaultData.defaultMaterials
        )
        assertTrue(prompt.contains("Biển mặt tiền Showroom Cafe"))
        assertTrue(prompt.contains("Dài 5.00m x Rộng 2.00m"))
        assertTrue(prompt.contains(adDescription))
        assertTrue(prompt.contains("Quận 1, TP.HCM"))
    }

    @Test
    fun testSanitizeChatTurnsAndErrorFormatting() {
        val messages = listOf(
            com.example.model.ChatMessage(isUser = false, text = "Xin chào!"),
            com.example.model.ChatMessage(isUser = true, text = "Biển Alu 10m2 cần mấy tấm?"),
            com.example.model.ChatMessage(isUser = false, text = "Khoảng 4 tấm tiêu chuẩn 1.22x2.44m."),
            com.example.model.ChatMessage(isUser = true, text = "Lỗi thử"),
            com.example.model.ChatMessage(isUser = false, text = "Lỗi mạng", isError = true, retryQuery = "Lỗi thử"),
            com.example.model.ChatMessage(isUser = true, text = "Dùng Alu Triều Chen ngoài trời được không?"),
            com.example.model.ChatMessage(isUser = false, text = "Được, chọn loại PVDF 0.21mm.")
        )

        val turns = com.example.ai.GeminiMaterialSuggestionService.sanitizeChatTurns(messages)
        assertEquals(4, turns.size)
        assertTrue(turns[0].first)
        assertEquals("Biển Alu 10m2 cần mấy tấm?", turns[0].second)
        assertTrue(!turns[1].first)
        assertTrue(turns[2].first)
        assertEquals("Dùng Alu Triều Chen ngoài trời được không?", turns[2].second)
        assertTrue(!turns[3].first)

        val err403 = com.example.ai.GeminiMaterialSuggestionService.formatFriendlyAiError(
            IllegalStateException("HTTP 403 App Check token rejected")
        )
        assertTrue(err403.contains("App Check"))

        val err429 = com.example.ai.GeminiMaterialSuggestionService.formatFriendlyAiError(
            IllegalStateException("HTTP 429 RESOURCE_EXHAUSTED")
        )
        assertTrue(err429.contains("quá tải"))
    }

    @Test
    fun testExpandedAdvertisingSoftwareGuides() {
        val guides = DefaultData.academyGuides
        val ids = guides.map { it.id }.toSet()

        assertTrue(guides.size >= 10)
        assertTrue(ids.contains("maintop"))
        assertTrue(ids.contains("coreldraw"))
        assertTrue(ids.contains("photoprint"))
        assertTrue(ids.contains("printexp_byhx"))
        assertTrue(ids.contains("riin_uv"))
        assertTrue(ids.contains("illustrator"))
        assertTrue(ids.contains("artcam_jd"))
        assertTrue(ids.contains("rdworks_lightburn"))
        assertTrue(ids.contains("led_software"))

        guides.forEach { guide ->
            assertTrue("Guide ${guide.id} must have title", guide.title.isNotBlank())
            assertTrue("Guide ${guide.id} must have steps", guide.steps.isNotEmpty())
            assertTrue("Guide ${guide.id} must have duration > 0", guide.durationMinutes > 0)
            assertTrue("Guide ${guide.id} must have prerequisites", guide.prerequisites.isNotBlank())
            assertTrue("Guide ${guide.id} must have shortcutsAndSpecs", guide.shortcutsAndSpecs.isNotEmpty())
            assertTrue("Guide ${guide.id} must have commonMistakes", guide.commonMistakes.isNotEmpty())
            assertTrue("Guide ${guide.id} must have practiceExercise", guide.practiceExercise.isNotBlank())
            guide.steps.forEach { step ->
                assertTrue(
                    "Step ${guide.id}#${step.stepNumber} must have detailedChecklist",
                    step.detailedChecklist.isNotEmpty()
                )
                assertTrue(
                    "Step ${guide.id}#${step.stepNumber} must have expectedResult",
                    step.expectedResult.isNotBlank()
                )
                assertTrue(
                    "Step ${guide.id}#${step.stepNumber} must have proTip",
                    step.proTip.isNotBlank()
                )
            }
        }
    }

    @Test
    fun testSmartQuoteSummaryAndMinOneM2() {
        val smallSign = com.example.calculator.SmartQuoteLine(
            title = "Biển vẫy nhỏ",
            widthMeters = 0.6,
            heightMeters = 0.8,
            rawDisplaySize = "60x80 cm",
            quantity = 2,
            unitPricePerM2 = 500000.0,
            minOneM2PerPiece = true,
            extraProcessingFeePerPiece = 50000.0
        )
        // Raw area = 0.48m2 -> billable 1.0m2/piece * 2 = 2.0m2. Line total = 2*500k + 2*50k = 1,100,000
        assertEquals(0.96, smallSign.totalRawArea, 0.001)
        assertEquals(2.0, smallSign.totalBillableArea, 0.001)
        assertEquals(1100000.0, smallSign.lineTotal, 0.01)

        val summary = com.example.calculator.SmartMaterialCalculator.calculateQuoteSummary(
            lines = listOf(smallSign),
            discountPercent = 10.0,
            vatPercent = 8.0
        )
        assertEquals(1100000.0, summary.subtotal, 0.01)
        assertEquals(110000.0, summary.discountAmount, 0.01)
        // After discount = 990,000 -> VAT 8% = 79,200 -> Grand = 1,069,200
        assertEquals(79200.0, summary.vatAmount, 0.01)
        assertEquals(1069200.0, summary.grandTotal, 0.01)
    }

    @Test
    fun testSheetCuttingAndRollOptimization() {
        // Sheet 1220x2440mm cutting 600x400mm pieces (kerf 4mm) -> fits nicely
        val opt = com.example.calculator.SmartMaterialCalculator.optimizeSheetCutting(
            pieceWidthMm = 600.0,
            pieceHeightMm = 400.0,
            quantity = 20,
            sheetWidthMm = 1220.0,
            sheetHeightMm = 2440.0,
            kerfMm = 4.0,
            sheetPrice = 360000.0
        )
        assertTrue(opt.fitsSingleSheet)
        assertTrue(opt.piecesPerSheet >= 12)
        assertTrue(opt.sheetsNeeded == 2)
        assertTrue(opt.efficiencyPercent > 50.0)
        assertEquals(720000.0, opt.totalSheetCost, 0.01)

        // Roll optimization: 0.7m x 1.2m on 1.52m roll -> fits 2 across (0.7*2 = 1.4 <= 1.52)
        val roll = com.example.calculator.SmartMaterialCalculator.optimizeRollUsage(
            pieceWidthM = 0.7,
            pieceHeightM = 1.2,
            quantity = 4,
            rollWidthM = 1.52,
            pricePerM2 = 50000.0
        )
        assertTrue(!roll.needsTiling)
        assertEquals(2, roll.acrossCount)
        assertEquals(2.4, roll.linearMetersNeeded, 0.01)

        // Frame & LED safety 80% rule: 300 LED * 1.2W = 360W / 0.8 = 450W -> needs 2x 400W power supplies
        val hw = com.example.calculator.SmartMaterialCalculator.calculateFrameAndLed(
            widthM = 4.0,
            heightM = 1.5,
            gridSpacingM = 0.6,
            ledCount = 300,
            ledWattPerUnit = 1.2
        )
        assertTrue(hw.steelBars6m > 0)
        assertEquals(450.0, hw.ledTotalWattsWithSafety, 0.1)
        assertEquals(2, hw.recommendedPower12V33ACount)
    }

    @Test
    fun testDesign3dQuoteSummaryAndPromptBuilder() {
        val lines = listOf(
            com.example.model.Design3dQuoteLine(
                id = 1L,
                name = "Mặt dựng Alu PVDF 3mm",
                spec = "Khổ 5m x 1.8m",
                unit = "m²",
                quantity = 9.0,
                unitPrice = 700000.0
            ),
            com.example.model.Design3dQuoteLine(
                id = 2L,
                name = "Chữ nổi Inox Vàng LED",
                spec = "Chân nổi 5cm",
                unit = "Bộ",
                quantity = 1.0,
                unitPrice = 3700000.0
            )
        )
        // Subtotal = 6,300,000 + 3,700,000 = 10,000,000
        val terms = com.example.model.Design3dQuoteTerms(
            discountPercent = 10.0,
            vatPercent = 8.0,
            depositPercent = 40
        )
        val summary = com.example.model.Design3dQuoteSummary.calculate(lines, terms)
        assertEquals(10_000_000.0, summary.subtotal, 0.01)
        assertEquals(1_000_000.0, summary.discountAmount, 0.01)
        assertEquals(9_000_000.0, summary.afterDiscount, 0.01)
        assertEquals(720_000.0, summary.vatAmount, 0.01)
        assertEquals(9_720_000.0, summary.grandTotal, 0.01)
        assertEquals(3_888_000.0, summary.depositAmount, 0.01)
        assertEquals(5_832_000.0, summary.balanceAmount, 0.01)

        val prompt = com.example.ai.GeminiDesign3dService.buildDesign3dPrompt(
            config = com.example.model.Design3dPromptConfig(
                brandText = "LUXURY SPA",
                widthM = 5.0,
                heightM = 1.8,
                style = com.example.model.Design3dStyle.ALU_INOX_LED
            ),
            hasReferenceImage = true
        )
        assertTrue(prompt.contains("LUXURY SPA"))
        assertTrue(prompt.contains("5.00m x Cao 1.80m"))
        assertTrue(prompt.contains("ẢNH HIỆN TRẠNG"))
    }

    @Test
    fun testAllThreeBoardDesignersCalculation() {
        // 1. Tarp Banner 4m x 1.2m = 4.8m2
        val tarpSpec = com.example.model.BoardDesignSpec(
            family = com.example.model.BoardDesignFamily.TARP_BANNER,
            brandText = "SIÊU THỊ ĐIỆN MÁY",
            widthMeters = 4.0,
            heightMeters = 1.2,
            quantity = 1,
            tarpProduct = com.example.model.TarpProductType.NO_SEAM_UV,
            tarpFinish = com.example.model.TarpFinishMode.IRON_FRAME_SIGN,
            discountPercent = 10.0,
            vatPercent = 8.0
        )
        val tarpCalc = com.example.calculator.SignageBoardDesignerCalculator.calculate(tarpSpec)
        assertEquals(4.8, tarpCalc.totalRawArea, 0.01)
        assertEquals(4.8, tarpCalc.totalBillableArea, 0.01)
        assertTrue(tarpCalc.rollOrSheetHint.contains("Khổ cuộn bạt tối ưu"))
        assertTrue(tarpCalc.grandTotal > 0.0)
        assertTrue(tarpCalc.discountAmount > 0.0)
        assertTrue(tarpCalc.vatAmount > 0.0)

        // 2. Alu + 3D Letter 6m x 1.5m = 9.0m2
        val aluSpec = com.example.model.BoardDesignSpec(
            family = com.example.model.BoardDesignFamily.ALU_3D_LETTER,
            brandText = "LUXURY SPA",
            widthMeters = 6.0,
            heightMeters = 1.5,
            quantity = 1,
            aluPanel = com.example.model.AluPanelType.ALCOREST_OUTDOOR,
            letter3d = com.example.model.Letter3dType.INOX_304_GOLD,
            letterLighting = com.example.model.LetterLightingStyle.HALO_BACKLIT,
            letterCoveragePercent = 30
        )
        val aluCalc = com.example.calculator.SignageBoardDesignerCalculator.calculate(aluSpec)
        assertEquals(9.0, aluCalc.totalRawArea, 0.01)
        assertTrue(aluCalc.lines.size >= 3)
        assertTrue(aluCalc.rollOrSheetHint.contains("tấm Alu chuẩn 1.22x2.44m"))
        assertTrue(aluCalc.materialPlanSummary.contains("LED"))

        // 3. Decal small sticker 0.6m x 0.8m = 0.48m2 -> min 1m2 = 1.0m2 per piece x 2 = 2.0m2
        val decalSpec = com.example.model.BoardDesignSpec(
            family = com.example.model.BoardDesignFamily.DECAL_STICKER,
            brandText = "OPEN 24/7",
            widthMeters = 0.6,
            heightMeters = 0.8,
            quantity = 2,
            minOneM2PerPiece = true,
            decalProduct = com.example.model.DecalProductType.DECAL_MILK_WHITE,
            decalLamination = com.example.model.DecalLaminationType.MATTE,
            decalBacking = com.example.model.DecalBackingType.FORMEX_5MM,
            includeOnSiteInstall = true
        )
        val decalCalc = com.example.calculator.SignageBoardDesignerCalculator.calculate(decalSpec)
        assertEquals(0.96, decalCalc.totalRawArea, 0.01)
        assertEquals(2.0, decalCalc.totalBillableArea, 0.01)
        assertTrue(decalCalc.lines.size >= 3)
        assertTrue(decalCalc.rollOrSheetHint.contains("Khổ cuộn Decal tối ưu"))
    }

    @Test
    fun testEstimatorAreaAndCustomQuoteCalculation() {
        val input = EstimationInput(
            category = SignageCategory.CNC_LASER,
            selectedCategoryOptionId = "custom_tranh_dien",
            customCategoryLabel = "Hộp Đèn Tranh Điện Siêu Mỏng",
            itemName = "Tranh Điện Menu Spa 60x80cm",
            projectLocation = "Quầy lễ tân tầng 1",
            width = 0.6,
            height = 0.8,
            quantity = 2,
            wastagePercent = 10.0,
            minOneM2PerPiece = true,
            customUnitPriceOverridePerM2 = 900000.0,
            discountPercent = 10.0,
            vatPercent = 8.0
        )
        val result = SignageCalculator.calculate(input, DefaultData.defaultUnitPrices)

        // singlePieceArea = 0.48 m2, netArea = 0.96 m2, min 1m2 -> billableArea = 2.0 m2
        assertEquals(0.48, result.singlePieceArea, 0.001)
        assertEquals(0.96, result.netArea, 0.001)
        assertEquals(2.0, result.billableArea, 0.001)
        // subtotal = 2.0 * 900,000 = 1,800,000; discount 10% = 180,000; afterDiscount = 1,620,000; VAT 8% = 129,600; final = 1,749,600
        assertEquals(1800000.0, result.subtotalCustomerPrice, 0.01)
        assertEquals(180000.0, result.discountAmount, 0.01)
        assertEquals(129600.0, result.vatAmount, 0.01)
        assertEquals(1749600.0, result.finalCustomerPrice, 0.01)
        assertEquals("Hộp Đèn Tranh Điện Siêu Mỏng", result.effectiveCategoryName)
    }
}

