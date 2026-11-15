package com.example.data

import com.example.model.AcademyGuide
import com.example.model.GuideStep

object AcademyCncLedData {

    val productionGuides: List<AcademyGuide> = listOf(
        AcademyGuide(
            id = "illustrator",
            title = "Adobe Illustrator (AI) — Chuẩn Hóa File Quảng Cáo & Bế Tem Chuyên Nghiệp",
            category = "Thiết Kế Đồ Họa",
            level = "Cầm Tay Chỉ Việc",
            iconName = "design",
            durationMinutes = 35,
            prerequisites = "Adobe Illustrator CC 2020 trở lên (hỗ trợ Large Canvas khổ lớn tới 57m), hiểu cơ bản về hệ màu CMYK & Layer.",
            summary = "Quy trình xử lý chuyên sâu file thiết kế từ khách hàng/agency gửi xuống xưởng: Artboard khổ lớn, Embed ảnh tránh mất link, Create Outlines, gán Spot Color CutContour và xuất PDF/X chuẩn RIP.",
            shortcutsAndSpecs = listOf(
                "Ctrl + Shift + O" to "Create Outlines (chuyển toàn bộ font chữ thành vector)",
                "Window -> Links -> Embed" to "Nhúng ảnh vào file để không báo lỗi Missing Link",
                "Object -> Expand Appearance" to "Mở rộng hiệu ứng viền Stroke/Gradient thành vector",
                "Swatches -> Spot Color" to "Đặt tên chính xác `CutContour` để PhotoPrint nhận bế",
                "PDF/X-1a hoặc High Quality" to "Chuẩn xuất PDF giữ nguyên màu CMYK & đường bế"
            ),
            commonMistakes = listOf(
                "Mở file AI trên máy xưởng bị báo 'Could not find linked file': Do người thiết kế chỉ Link ảnh ngoài chứ chưa Embed Image vào file.",
                "Đường CutContour tự động bị tô nền hoặc cắt sai lớp: Do quên đưa CutContour lên Layer trên cùng và tắt Fill (chỉ để Stroke 0.25pt)."
            ),
            practiceExercise = "Bài tập thực hành: Mở một file thiết kế AI có chứa ảnh bitmap và chữ nghệ thuật, thực hiện Embed toàn bộ ảnh, Create Outlines, tạo thêm Layer `CutContour` dạng Spot Color rồi xuất file PDF chuẩn sẵn sàng nạp vào PhotoPrint.",
            steps = listOf(
                GuideStep(
                    stepNumber = 1,
                    title = "Thiết Lập Artboard Khổ Lớn (Large Canvas) & Hệ Màu CMYK",
                    content = "Khắc phục hoàn toàn giới hạn kích thước 5.7m của bản AI cũ bằng tính năng Large Canvas trên AI CC 2020+ để dựng biển hiệu cỡ lớn đúng tỉ lệ 1:1.",
                    detailedChecklist = listOf(
                        "Nhấn Ctrl + N -> đặt đơn vị Centimeters hoặc Millimeters -> Color Mode: **CMYK Color** -> Raster Effects: **Medium (150 ppi)** cho biển lớn hoặc **High (300 ppi)** cho tem nhãn.",
                        "Nếu biển lớn hơn 5.7m, tạo Artboard mới với kích thước lớn trực tiếp để AI tự kích hoạt chế độ Large Canvas.",
                        "Kiểm tra bảng Document Setup (Ctrl + Alt + P) xác nhận kích thước chính xác 100%."
                    ),
                    expectedResult = "Artboard hiển thị đúng tỉ lệ kích thước thật của công trình với hệ màu CMYK.",
                    proTip = "Nếu phải lưu file cho đối tác dùng máy cũ (CS6) hoặc nhập vào CorelDRAW đời thấp, chọn Save As -> Illustrator CS6 hoặc PDF.",
                    techNote = "Tắt tính năng Overprint Fill ở chữ màu trắng (`Window -> Attributes`) để chữ trắng không bị biến mất hoàn toàn khi qua phần mềm RIP!"
                ),
                GuideStep(
                    stepNumber = 2,
                    title = "Nhúng Ảnh (Embed), Rã Font (Create Outlines) & Gộp Vector",
                    content = "Đảm bảo file mang xuống máy in hoặc máy CNC không bao giờ bị thiếu ảnh, thiếu font hay lỗi độ dày nét viền.",
                    detailedChecklist = listOf(
                        "Mở `Window -> Links`: chọn toàn bộ ảnh trong danh sách -> bấm menu 3 gạch góc trên phải -> chọn **Embed Image(s)**.",
                        "Nhấn Ctrl + A -> nhấn **Ctrl + Shift + O** để chuyển toàn bộ Text sang vector Outlines.",
                        "Vào **Object -> Expand Appearance** rồi **Object -> Expand** (tích Fill & Stroke) nếu chuẩn bị xuất file cho cắt CNC/Laser.",
                        "Dùng bảng **Pathfinder -> Unite** để hàn liền các chữ nét uốn lượn giao nhau trước khi xuất `.DXF`."
                    ),
                    expectedResult = "Danh sách Links hiện biểu tượng đã nhúng cho 100% ảnh, không còn chữ sống trong bảng Find Font.",
                    proTip = "Dùng `Select -> Same -> Appearance` để kiểm tra nhanh các đối tượng còn màu RGB sót lại và đổi về CMYK.",
                    techNote = "Kiểm tra mục Effective PPI trong bảng Links: ảnh nhỏ dưới 50 PPI khi phóng kích thước thật sẽ bị vỡ hạt rõ rệt khi in."
                ),
                GuideStep(
                    stepNumber = 3,
                    title = "Tạo Spot Color CutContour & Xuất PDF Chuẩn In + Bế",
                    content = "Quy chuẩn quốc tế giúp máy in & máy cắt bế decal phối hợp tự động chuẩn xác.",
                    detailedChecklist = listOf(
                        "Tạo Layer mới trên cùng đặt tên `CutContour`. Copy đường viền cần cắt dán vào Layer này (`Ctrl + F`).",
                        "Mở `Window -> Swatches` -> New Swatch -> Swatch Name gõ chính xác `CutContour`, Color Type chọn **Spot Color**, đặt màu 100% Magenta để dễ phân biệt.",
                        "Gán màu `CutContour` cho Stroke (độ dày 0.25pt) và tắt màu nền Fill (`None`).",
                        "Vào `File -> Save As -> Adobe PDF` -> Preset: High Quality Print -> mục Compression chọn **Do Not Downsample** rồi lưu file."
                    ),
                    expectedResult = "Xuất thành công file PDF giữ trọn vẹn màu CMYK sắc nét và đường Spot Color bế tem.",
                    proTip = "Nên đặt tên file PDF kèm kích thước và số lượng (VD: `tem_tra_sua_5x5cm_SL500.pdf`) để thợ đứng máy khỏi phải hỏi lại.",
                    techNote = "Tuyệt đối không viết sai chính tả tên Spot Color `CutContour` (chú ý viết hoa chữ C thứ 1 và chữ C thứ 2)."
                )
            )
        ),
        AcademyGuide(
            id = "artcam_jd",
            title = "ArtCAM / Vectric Aspire / JDPaint — Xuất Đường Dao CNC Phay Alu, Mica",
            category = "Phần Mềm CNC & Laser",
            level = "Cầm Tay Chỉ Việc",
            iconName = "cnc",
            durationMinutes = 45,
            prerequisites = "File vector `.DXF` (AutoCAD 2004) hoặc `.EPS` đã xử lý sạch từ CorelDRAW/AI, nắm rõ đường kính dao phay đang lắp trên trục Spindle.",
            summary = "Học chi tiết từ nhập file DXF, sửa lỗi hở nét Vector Doctor, lập trình đường dao cắt đứt 2D Profile Mica/Alu, phay rãnh V 90° gấp hộp Alu, đặt cầu giữ chữ nhỏ và xuất file G-Code.",
            shortcutsAndSpecs = listOf(
                "Khổ phôi chuẩn tấm" to "1220mm x 2440mm (gốc X0, Y0 góc dưới trái)",
                "Dao 1 me Mica 3mm" to "Phi 3.175mm | 18,000 RPM | Feed 2500–3200 mm/min",
                "Dao phay rãnh gấp Alu" to "Dao V 90° | Sâu 2.1–2.2mm (trên tấm Alu 3mm)",
                "2D Profile Outside/Inside" to "Biên ngoài chữ chọn Outside; lỗ trong chữ O/A/B chọn Inside",
                "Tabs / Bridges" to "Cầu giữ cao 1–1.2mm chống văng chữ nhỏ < 5cm"
            ),
            commonMistakes = listOf(
                "Bị mất chữ hoặc kích thước chữ bị hụt nhỏ đi 3mm: Do chọn nhầm đường chạy dao là Inside hoặc Along/On Vector thay vì Outside cho biên ngoài chữ.",
                "Chữ nhỏ bị dao cuốn văng gãy mũi dao: Quên đặt Tabs/Bridges (cầu giữ phôi) ở 2–3 điểm trên mỗi chữ nhỏ.",
                "Gấp hộp Alu bị nứt lớp nhôm mặt ngoài hoặc gãy luôn tấm: Đặt chiều sâu phay V-Bit quá sâu (> 2.4mm trên tấm 3mm) hoặc quá nông (< 1.8mm)."
            ),
            practiceExercise = "Bài tập thực hành: Nhập file DXF bộ chữ nổi Mica cao 15cm và khung hộp Alu 80x40cm vào ArtCAM/Aspire; tạo 2 đường dao tách biệt: đường dao V90 sâu 2.15mm để gấp hộp Alu và đường cắt đứt 2D Profile Outside có đặt Tabs giữ phôi, mô phỏng 3D và xuất file `.nc`/`.tap`.",
            steps = listOf(
                GuideStep(
                    stepNumber = 1,
                    title = "Khởi Tạo Phôi Tấm 1.22x2.44m, Nhập DXF & Nối Vector Hở",
                    content = "Một đường vector bị hở hoặc bị 2 nét chồng lên nhau sẽ khiến phần mềm CAM báo lỗi không xuất được dao hoặc đầu dao cày đi cày lại làm cháy tấm phôi.",
                    detailedChecklist = listOf(
                        "Tạo Model/Job mới kích thước chuẩn `Width = 1220mm`, `Height = 2440mm`, độ dày phôi `Z = 3mm` (hoặc 5mm tùy tấm), gốc tọa độ `X0, Y0` ở góc dưới bên trái, `Z Zero` trên mặt phôi.",
                        "Import file `.DXF` (AutoCAD 2004) hoặc `.EPS` vào vùng phôi.",
                        "Chạy công cụ **Vector Doctor** (ArtCAM) hoặc **Vector Validator / Join Open Vectors** (Aspire) để tự động nối kín các điểm hở và xóa nét trùng (Delete Duplicates)."
                    ),
                    expectedResult = "100% đường nét chữ là vòng khép kín hợp lệ, nằm gọn trong khung 1220x2440mm cách mép tối thiểu 15mm.",
                    proTip = "Dùng chức năng **Nesting** trong Aspire/ArtCAM để tự động xếp các chữ lồng vào khoảng trống của nhau, tiết kiệm 15–25% phôi Mica đắt tiền.",
                    techNote = "Luôn chừa khoảng hở giữa các đối tượng tối thiểu bằng 2.5 lần đường kính dao (ví dụ dao 3.175mm thì xếp chữ cách nhau >= 8–10mm)."
                ),
                GuideStep(
                    stepNumber = 2,
                    title = "Lập Trình Cắt Đứt 2D Profile, Phay Rãnh V90 Alu & Đặt Cầu Giữ Tabs",
                    content = "Tách riêng từng loại dao và từng chiều sâu cắt để xuất file gọn gàng, an toàn tuyệt đối cho trục Spindle.",
                    detailedChecklist = listOf(
                        "Cắt chữ Mica/Formex/Alu: chọn toolpath **2D Profile**. Chọn đường biên ngoài chữ -> đặt **Outside**; chọn riêng các lỗ trong chữ O, A, B, P -> đặt **Inside**.",
                        "Đặt Finish Depth sâu hơn độ dày tấm 0.3–0.5mm (tấm 3mm cắt sâu 3.4mm) để cắt đứt sạch lớp màng đáy mà không ăn quá sâu vào tấm lót bàn CNC.",
                        "Bật **Add Tabs / Bridges** (dài 4mm, dày 1.0–1.2mm, mỗi chữ 2–3 cầu) cho các chi tiết chữ nhỏ tránh bị văng gãy dao.",
                        "Phay rãnh gấp mí tấm Alu 3mm: Chọn đường gấp -> **Profile Toolpath -> Along/On Vector**, chọn dao **V-Bit 90°**, đặt sâu **2.15mm**."
                    ),
                    expectedResult = "Các đường chạy dao hiển thị rõ mũi tên hướng đi; phay rãnh V90 chừa đúng lớp nhôm mặt ngoài ~0.8mm gập vuông sắc nét.",
                    proTip = "Cắt Mica trong để làm mặt chữ nổi, nên chọn chế độ chạy cuối cùng (Last Pass) giữ tốc độ đều và bật Ramp vào dao xoắn ốc để không bị vết khuyết ở điểm xuống dao.",
                    techNote = "Không xuất chung file chạy của dao V-Bit 90° và dao cắt thẳng phi 3.175mm vào cùng 1 file G-Code nếu máy không có bộ thay dao tự động (ATC)."
                ),
                GuideStep(
                    stepNumber = 3,
                    title = "Mô Phỏng 3D (Simulate) & Xuất File G-Code Cho Mach3 / NCStudio",
                    content = "Luôn kiểm tra mô phỏng trên màn hình trước khi mang file xuống máy CNC thật.",
                    detailedChecklist = listOf(
                        "Nhấn **Simulate All Toolpaths** -> xoay góc nhìn 3D kiểm tra xem chữ cắt ra có bị mất nét, mất lỗ trong hay rách rãnh gấp hay không.",
                        "Bấm **Save Toolpaths** -> chọn đúng Post Processor theo tủ điện máy CNC của xưởng: `NCStudio (*.nc)` cho card NCStudio V5/V8; `Mach2/3 Arcs (mm) (*.tap)` cho Mach3; `G-Code (mm) (*.mmg)` cho tay cầm DSP.",
                        "Đặt tên file rõ mũi dao & độ sâu (VD: `chu_mica_3mm_dao1me3175.nc`)."
                    ),
                    expectedResult = "Mô phỏng 3D chính xác từng chi tiết chữ, file G-Code xuất ra đọc đúng trên máy CNC.",
                    proTip = "Trước khi hạ dao cắt thật, cho máy chạy thử đường biên (Border/Dry Run) ở độ cao Z+20mm để chắc chắn không chạm vít bắt giữ tấm.",
                    techNote = "Luôn bật máy hút bụi công nghiệp và xịt khí nén/cồn giải nhiệt khi cắt nhôm/inox để tránh dính phoi làm gãy dao."
                )
            )
        ),
        AcademyGuide(
            id = "rdworks_lightburn",
            title = "RDWorks / LightBurn — Phần Mềm Cắt Laser CO2 Mica & Khắc Kim Loại",
            category = "Phần Mềm CNC & Laser",
            level = "Cầm Tay Chỉ Việc",
            iconName = "cnc",
            durationMinutes = 35,
            prerequisites = "Máy cắt Laser CO2 (bóng 80W–150W) kết nối USB/LAN, máy làm mát nước Chiller bật ổn định 18–24°C, quạt hút khói và bơm khí nén.",
            summary = "Thành thạo phân tầng Layer màu Cắt/Khắc, bảng thông số Speed & Power chuẩn cho Mica 2–10mm không cháy xám cạnh, tối ưu thứ tự cắt trong-ngoài và căn khung Frame an toàn.",
            shortcutsAndSpecs = listOf(
                "Mica 2mm (Bóng 100W)" to "Speed 18–22 mm/s | Power 55–65%",
                "Mica 3mm (Bóng 100W)" to "Speed 12–15 mm/s | Power 68–75%",
                "Mica 5mm (Bóng 100W)" to "Speed 7–9 mm/s | Power 80–85%",
                "Khắc Scan Mica / Gỗ" to "Speed 250–350 mm/s | Power 18–25% | Interval 0.08mm",
                "Origin -> Frame / Go Scale" to "Chạy thử 4 cạnh kiểm tra vùng cắt trên tấm phôi"
            ),
            commonMistakes = listOf(
                "Cạnh Mica cắt xong bị mờ trắng hoặc cháy sém răng cưa: Do đặt tốc độ quá chậm hoặc thổi khí nén quá mạnh khi cắt Mica (chỉ cần gió nhẹ dập lửa).",
                "Cắt xong rớt lỗ chữ bên trong ra trước làm lệch chữ khi cắt viền ngoài: Do chưa sắp xếp thứ tự Layer Khắc và Lỗ Trong cắt trước, Viền Ngoài cắt sau."
            ),
            practiceExercise = "Bài tập thực hành: Nhập file bảng tên công ty Mica có logo khắc mờ (Scan) và 4 lỗ bắt vít + đường viền bo góc (Cut) vào RDWorks/LightBurn, gán 2 layer màu với thứ tự Scan -> Cắt lỗ -> Cắt viền ngoài và chạy Frame kiểm tra.",
            steps = listOf(
                GuideStep(
                    stepNumber = 1,
                    title = "Phân Tầng Layer Màu (Scan Khắc & Cut Cắt) Theo Thứ Tự Thông Minh",
                    content = "Trong phần mềm Laser, màu sắc của nét vẽ chính là lệnh quy định máy sẽ chạy Khắc (Scan) hay Cắt Đứt (Cut) và chạy cái nào trước.",
                    detailedChecklist = listOf(
                        "Import file `.DXF` / `.AI` -> bôi đen phần logo/chữ khắc mờ gán Layer màu Đen -> chọn Mode = **Scan**.",
                        "Bôi đen các lỗ khoan vít hoặc ruột chữ (O, A, B) gán Layer màu Xanh dương -> chọn Mode = **Cut**.",
                        "Bôi đen đường bao ngoài cùng gán Layer màu Đỏ -> chọn Mode = **Cut**.",
                        "Sắp xếp thứ tự danh sách Layer từ trên xuống dưới: `1. Scan (Khắc)` -> `2. Cut Lỗ Trong` -> `3. Cut Viền Ngoài` (hoặc bật `Cut Inner Shapes First` trong Optimization Settings)."
                    ),
                    expectedResult = "Thứ tự chạy logic: khắc xong hoàn toàn trên tấm phôi phẳng rồi mới cắt lỗ và cắt đứt rơi sản phẩm.",
                    proTip = "Bật thêm `Laser Offset / Kerf` (khoảng 0.1–0.15mm) nếu làm các bộ chữ âm bản lồng khít mặt mica vào khung inox/alu.",
                    techNote = "Với chữ Mica cắt ghép vào chân chữ nổi, nhớ bóc lớp giấy bảo vệ mặt dưới hoặc thổi khí sạch tránh bùng lửa dưới khe bàn tổ ong."
                ),
                GuideStep(
                    stepNumber = 2,
                    title = "Cài Thông Số Speed & Power, Chạy Thử Khung Frame & An Toàn Chiller",
                    content = "Đặt đúng tỉ lệ tốc độ và công suất giúp cạnh Mica bóng trong suốt như pha lê mà lại tiết kiệm tuổi thọ bóng thủy tinh Laser.",
                    detailedChecklist = listOf(
                        "Nhập Speed & Power theo độ dày Mica thực tế (VD Mica 3mm: Speed 13 mm/s, Min/Max Power 70%–75%; không đặt Max Power quá 85–90% liên tục để giữ bền bóng).",
                        "Đặt tấm Mica phẳng trên bàn, chỉnh tiêu cự đầu Laser bằng thước chuẩn (thường 6–8mm từ đầu bép xuống mặt Mica).",
                        "Dời đầu Laser bằng phím mũi tên về góc gốc phôi -> nhấn **Origin** -> nhấn **Frame / Go Scale** xem tia đỏ chạy vòng quanh kiểm tra không bị tràn mép tấm.",
                        "Kiểm tra nhiệt kế máy làm mát Chiller (18–24°C) và bật quạt hút khói rồi mới nhấn **Start**."
                    ),
                    expectedResult = "Đường cắt xuyên ngọt 1 lượt, cạnh mica bóng láng không cháy sém, phôi không bị lẹm mép.",
                    proTip = "Bật chế độ `Ramp / Lead-in` nhẹ hoặc giảm Min Power ở các góc nhọn để tia laser không dừng cháy to một chấm đen tại điểm rẽ góc.",
                    techNote = "Tuyệt đối KHÔNG bao giờ rời khỏi máy Laser trong khi đang cắt Mica vì nhựa acrylic có thể bùng cháy ngọn lửa rất nhanh nếu mất gió nén."
                )
            )
        ),
        AcademyGuide(
            id = "led_software",
            title = "HD2020 / LedShowTW / LedEdit — Lập Trình Biển LED Ma Trận & LED Full Màu",
            category = "Phần Mềm LED & Điều Khiển",
            level = "Cầm Tay Chỉ Việc",
            iconName = "bolt",
            durationMinutes = 35,
            prerequisites = "Card điều khiển LED (HD-W60 / HD-U6A / BX / T-1000S), xác định chính xác kích thước module (P10 16x32cm hay P5) và kiểu quét Scan Mode.",
            summary = "Hướng dẫn lập trình từ A–Z biển LED ma trận chữ chạy: tính pixel màn hình, chọn đúng mã card và kiểu quét, thiết kế chữ chạy nổi bật, sơ đồ đi dây LedEdit cho LED Full màu 1903/2811.",
            shortcutsAndSpecs = listOf(
                "1 tấm P10 (16x32cm)" to "Độ phân giải 32 ngang x 16 cao điểm ảnh (px)",
                "1 tấm P5 (16x32cm)" to "Độ phân giải 64 ngang x 32 cao điểm ảnh (px)",
                "Mật khẩu Wi-Fi card HD" to "Mặc định `88888888` (8 số 8)",
                "Pass cài đặt phần cứng" to "HD2020 thường dùng `168` hoặc `888`",
                "Cấp nguồn LED 5V" to "Bù nguồn dương/âm sau mỗi 50–100 bóng tránh ố đỏ"
            ),
            commonMistakes = listOf(
                "Nạp xong màn hình LED bị nhảy loạn chữ, chia làm 2–4 hàng lệch nhau: Do chọn sai thông số Scan Mode / OE Polarity trong phần Hardware Settings.",
                "Chạy chữ chạy quá nhanh hoặc font nét mảnh khó đọc từ xa: Do chọn font có chân quá gầy. Nên dùng font dày như Arial Black, UTM Avo Bold."
            ),
            practiceExercise = "Bài tập thực hành: Cấu hình màn hình LED ma trận P10 kích thước 4 tấm ngang x 2 tấm cao (128x32 px) trên HD2020, thêm hiệu ứng chữ chạy khuyến mãi + viền nháy đuổi và xuất file nạp USB/Wi-Fi.",
            steps = listOf(
                GuideStep(
                    stepNumber = 1,
                    title = "Khai Báo Độ Phân Giải Pixel & Mã Card Điều Khiển (HD2020 / LedShowTW)",
                    content = "Bước quan trọng nhất quyết định biển LED hiển thị đúng khung hình, không bị mất góc hay loạn chữ.",
                    detailedChecklist = listOf(
                        "Đếm số tấm LED ghép thực tế: VD biển ghép ngang 5 tấm x cao 2 tấm P10 (mỗi tấm 32x16 px) -> Tổng Width = `160 px`, Height = `32 px`.",
                        "Mở HD2020 -> Settings -> Screen Settings (nhập mật khẩu `168` nếu hỏi) -> chọn đúng model Card gắn trong hộp biển (VD `HD-W60-75`, `HD-U6A`) và loại màu (Single/Dual/Full).",
                        "Nhập Width = 160, Height = 32 -> chọn đúng Scan Mode (P10 ngoài trời thường là `P10 1/4 Scan`, có thể bấm nút `Smart Setting` để dò tự động)."
                    ),
                    expectedResult = "Khung soạn thảo hiển thị đúng tỉ lệ dài x rộng của biển hiệu thật ngoài xưởng.",
                    proTip = "Chụp ảnh lại mặt sau tem module P10 và mã Card trước khi đóng nắp hộp biển để sau này bảo hành nạp lại nội dung trong 1 phút.",
                    techNote = "Nếu dùng nguồn 5V cấp cho LED ma trận, tuyệt đối không đấu ngược cực +5V và GND vì sẽ nổ cháy ngay IC trên card và module."
                ),
                GuideStep(
                    stepNumber = 2,
                    title = "Thiết Kế Nội Dung Chữ Chạy, Viền Nháy & Nạp Qua Wi-Fi / USB",
                    content = "Biển LED ngoài trời người đi xe máy chỉ lướt qua trong 2–3 giây, nên chữ phải thật to, rõ, tương phản cao.",
                    detailedChecklist = listOf(
                        "Bấm `Add Program` -> thêm khung **Text** -> gõ nội dung ngắn gọn, chọn font chữ nét dày (UTM Avo Bold, Arial Black, Montserrat ExtraBold).",
                        "Chỉnh tốc độ (Speed) vừa mắt người đọc (~25–40), chọn hiệu ứng `Continuous Move Left` cho dòng chữ dài.",
                        "Thêm viền chạy nhẹ (`Frame / Border`) giúp thu hút ánh nhìn mà không che mất chữ.",
                        "Kết nối Wi-Fi phát ra từ Card (pass `88888888`) -> bấm **Send** gửi thẳng ra biển (hoặc bấm **U-Disk** chép vào USB rồi cắm vào cổng USB trên card)."
                    ),
                    expectedResult = "Chữ chạy mượt, rõ ràng sắc nét trên mặt biển LED, không nhấp nháy giật hình.",
                    proTip = "Với LED đúc Full màu (1903/2811) chạy viền biển hiệu hoặc trang trí mặt tiền: dùng **LedEdit** vẽ đúng sơ đồ đi dây thực tế (ziczac ngang/dọc) -> nạp video hiệu ứng -> Export file `.LED` chép vào thẻ nhớ mạch T-1000S.",
                    techNote = "Cài đặt thêm hẹn giờ độ sáng (Brightness Schedule) giảm còn 50–60% sau 22h đêm để không gây chói mắt người đi đường và tăng tuổi thọ bóng LED."
                )
            )
        ),
        AcademyGuide(
            id = "cnc_router",
            title = "Vận Hành Thực Tế Máy CNC Router Quảng Cáo (Dao, Spindle & Hút Chân Không)",
            category = "Gia Công Xưởng",
            level = "Cầm Tay Chỉ Việc",
            iconName = "settings",
            durationMinutes = 30,
            prerequisites = "Đã có file G-Code (`.nc`/`.tap`) xuất từ ArtCAM/Aspire, tấm phôi Mica/Alu/Formex đã đặt trên mặt bàn CNC.",
            summary = "Chọn đúng dao xoắn 1 me cho Mica, 2 me cho Alu, dao quả dứa cho Formex; lấy gốc phôi X0-Y0-Z0 chuẩn xác và cấu hình vòng quay Spindle chống chảy nhựa, gãy dao.",
            shortcutsAndSpecs = listOf(
                "Dao 1 me (Single Flute)" to "Chuyên cắt Mica, nhựa — thoát phoi nhanh không cháy mép",
                "Dao 2 me (Two Flute)" to "Chuyên cắt Alu, nhôm mềm, gỗ MDF",
                "Dao quả dứa (Corn Bit)" to "Chuyên cắt Formex / PVC Foam mịn không xơ",
                "Mica 3–5mm" to "Spindle 18,000–20,000 RPM | Feed 2,500–3,500 mm/min",
                "Alu 3mm" to "Spindle 16,000–18,000 RPM | Feed 1,800–2,200 mm/min"
            ),
            commonMistakes = listOf(
                "Quên mở bơm nước giải nhiệt trục Spindle trước khi chạy: Sau 10 phút Spindle nóng quá nhiệt gây cháy cuộn đồng.",
                "Lấy gốc Z0 quá sâu hoặc quên kẹp đầu dò: Dao lao thẳng xuống cày nát tấm lót bàn hút chân không."
            ),
            practiceExercise = "Bài tập thực hành: Lắp dao 1 me phi 3.175mm bằng collet ER20 sạch bụi, bật bơm nước làm mát Spindle, rà gốc X0/Y0/Z0 trên góc tấm Mica 3mm và cho máy chạy thử biên độ an toàn.",
            steps = listOf(
                GuideStep(
                    stepNumber = 1,
                    title = "Lựa Chọn Đúng Chủng Loại Dao Phay & Siết Collet An Toàn",
                    content = "Mỗi vật liệu quảng cáo đòi hỏi rãnh thoát phoi khác nhau. Chọn sai dao sẽ làm chảy dính nhựa hoặc sứt mẻ cạnh tấm.",
                    detailedChecklist = listOf(
                        "Mica trong / Mica sữa: lắp dao 1 me xoắn (Single Flute Spiral) phi 3.175mm hoặc 4mm hợp kim Vonfram bén sắc.",
                        "Alu / Nhôm / Đồng mỏng: lắp dao 2 me xoắn hoặc dao 1 me chuyên nhôm.",
                        "Formex / PVC Foam / Gỗ MDF: lắp dao quả dứa (Corn Teeth) hoặc dao 2 me thẳng.",
                        "Vệ sinh sạch mạt bụi trong áo kẹp Collet ER20, lắp dao ngập vào tối thiểu 2/3 chiều dài chuôi dao và siết chặt bằng 2 cờ lê."
                    ),
                    expectedResult = "Dao lắp đồng tâm không đảo, đúng chủng loại cho vật liệu đang gia công.",
                    proTip = "Tuyệt đối không dùng dao đã cắt gỗ/MDF hoặc đã cùn mang sang cắt Mica vì cạnh cắt sẽ đục mờ và chảy nhựa dính chặt vào rãnh cắt.",
                    techNote = "Kiểm tra bơm nước làm mát hoặc quạt giải nhiệt Spindle chạy ổn định trước khi bấm khởi động trục quay."
                ),
                GuideStep(
                    stepNumber = 2,
                    title = "Lấy Gốc Tọa Độ X0-Y0-Z0, Bật Bàn Hút Chân Không & Kiểm Soát Tốc Độ",
                    content = "Đảm bảo tấm phôi không bị xê dịch 1 milimét nào trong suốt quá trình phay.",
                    detailedChecklist = listOf(
                        "Bật máy bơm hút chân không Vacuum (khóa các van vùng không có phôi để tăng lực hút) hoặc bắn vít cố định 4 góc và giữa tấm.",
                        "Di chuyển đầu dao về góc dưới bên trái tấm phôi -> bấm `Clear X/Y` để đặt gốc X0, Y0.",
                        "Dùng cục cảm biến Z-Zero chạm mặt phôi (hoặc hạ nhẹ mũi dao vừa chạm tờ giấy mỏng trên mặt phôi) -> đặt gốc Z0.",
                        "Đặt tốc độ Spindle (16,000–20,000 RPM) và kéo thanh trượt Feed Rate ban đầu ở mức 70% để nghe tiếng cắt êm rồi mới tăng lên 100%."
                    ),
                    expectedResult = "Máy cắt mượt mà, phoi thoát đều, tấm phôi giữ chặt phẳng lì từ đầu đến cuối tấm.",
                    proTip = "Luôn đặt sẵn nút dừng khẩn cấp (Emergency Stop) trong tầm tay khi bấm lệnh Start trong 10 giây đầu tiên.",
                    techNote = "Bật máy hút bụi suốt ca cắt để gom sạch mạt nhôm và bụi mica, tránh lọt vào thanh trượt bi trục X/Y."
                )
            )
        ),
        AcademyGuide(
            id = "led_wiring",
            title = "Quy Chuẩn Đấu Nối Nguồn LED 12V Chống Sụt Áp & Cháy Nổ Ngoài Trời",
            category = "Gia Công Xưởng",
            level = "Điện Quảng Cáo",
            iconName = "bolt",
            durationMinutes = 30,
            prerequisites = "Module LED 12V (Samsung/TQ), nguồn tổ ong chống nước 12V 33A (400W), dây đôi đồng Cadivi 2x1.5mm hoặc 2x2.5mm.",
            summary = "Công thức tính chọn công suất nguồn an toàn hệ số 80%, sơ đồ đi dây mạch xương cá chống tối chữ cuối dải và kỹ thuật chống nước cho biển ngoài trời.",
            shortcutsAndSpecs = listOf(
                "Công thức tính nguồn" to "Tổng W = (Số LED x Công suất/con) / 0.8",
                "Nguồn 12V 33A (400W)" to "Tải an toàn tối đa ~320W (~260 con LED 1.2W)",
                "Giới hạn nối tiếp 1 dải" to "Tối đa 20–25 con LED / nhánh (không nối 50–100 con)",
                "Dây trục chính nguồn" to "Dây đồng Cadivi 2x1.5mm hoặc 2x2.5mm"
            ),
            commonMistakes = listOf(
                "Đấu nối tiếp 60–100 bóng LED thành 1 dây dài duy nhất: Bị sụt áp xuống dưới 10V khiến nửa chữ cuối tối mờ hoặc ngả vàng ố.",
                "Lắp nguồn tổ ong nằm ngửa hoặc đầu dây hướng lên trời: Nước mưa theo dây điện chảy vào bo mạch gây nổ tụ chập cháy."
            ),
            practiceExercise = "Bài tập thực hành: Tính số lượng nguồn 12V 33A cần thiết cho một bộ chữ nổi dùng 500 con LED Samsung 3 mắt (1.2W/con) và vẽ sơ đồ chia nhánh dây trục xương cá hợp lý.",
            steps = listOf(
                GuideStep(
                    stepNumber = 1,
                    title = "Tính Công Suất Nguồn Theo Quy Tắc An Toàn 80%",
                    content = "Nguồn LED chạy quá tải 100% sẽ nóng rực, phù tụ và cháy chỉ sau 3–6 tháng lắp ngoài trời.",
                    detailedChecklist = listOf(
                        "Đếm tổng số con LED dự kiến gắn trong chữ/hộp đèn và nhân với công suất mỗi bóng (LED 3 mắt Samsung thường 1.2W–1.5W/con).",
                        "Chia tổng công suất cho **0.8** để ra công suất nguồn tối thiểu cần mua (VD: 200 con x 1.2W = 240W; 240 / 0.8 = 300W -> Chọn nguồn 12V 33A 400W).",
                        "Nếu tổng tải > 320W, chia làm 2 hoặc nhiều nguồn 12V 33A độc lập thay vì dồn hết vào 1 nguồn."
                    ),
                    expectedResult = "Nguồn chạy mát tay, ánh sáng LED đồng đều rực rỡ suốt nhiều năm.",
                    proTip = "Gắn thêm Rơ-le Timer hẹn giờ (như KG316T) hoặc khởi động từ để tự động bật đèn 18h và tắt 23h đêm, vừa tiết kiệm điện cho khách vừa kéo dài gấp đôi tuổi thọ LED.",
                    techNote = "Không trộn lẫn dây LED 12V vào nguồn 5V hoặc cắm nhầm điện lưới 220V vào đầu ra DC của nguồn."
                ),
                GuideStep(
                    stepNumber = 2,
                    title = "Đấu Mạch Xương Cá Chống Sụt Áp & Lắp Nguồn Chống Mưa Dột",
                    content = "Điện áp DC 12V suy hao rất nhanh trên dây nhỏ của module LED. Cần kéo dây đồng tiết diện lớn chạy song song.",
                    detailedChecklist = listOf(
                        "Kéo đường dây trục chính bằng dây đồng Cadivi 2x1.5mm hoặc 2x2.5mm dọc theo khung biển hiệu.",
                        "Cứ mỗi cụm **20–25 bóng LED**, trích một nhánh câu song song (cực Dương vào Dương, Âm vào Âm) trực tiếp vào dây trục chính.",
                        "Hàn thiếc hoặc bấm cút nối chống nước rồi bọc kín bằng ống gen co nhiệt cho tất cả mối nối ngoài trời.",
                        "Treo nguồn thẳng đứng với chiều đầu dây điện chúc xuống dưới, đặt tại nơi khô ráo và khoan lỗ thoát nước phi 4–6mm dưới đáy chữ nổi/hộp biển."
                    ),
                    expectedResult = "Bóng đầu và bóng cuối của chữ sáng đều như nhau 100%, mối nối an toàn tuyệt đối dưới trời mưa bão.",
                    proTip = "Với hộp đèn chữ lớn dài > 3m, cấp điện vào cả 2 đầu của dây trục chính (đấu mạch vòng khép kín) để triệt tiêu hoàn toàn hiện tượng sụt áp.",
                    techNote = "Kiểm tra đồng hồ VOM hoặc sáng thử LED trước khi bơm keo gắn kín mặt Mica chữ nổi lên công trình."
                )
            )
        ),
        AcademyGuide(
            id = "letter_bending",
            title = "Kỹ Thuật Uốn Chân Chữ Inox / Nhôm Gờ F & Dán Mặt Mica Không Ố Keo",
            category = "Gia Công Xưởng",
            level = "Nghệ Nhân Thi Công",
            iconName = "build",
            durationMinutes = 30,
            prerequisites = "Mặt chữ Mica 2–3mm đã cắt CNC/Laser, cuộn chân nhôm gờ F hoặc băng Inox 304, keo lạnh tàng hình/keo AB, kẹp chữ C.",
            summary = "Quy trình gia công chữ nổi cao cấp tại xưởng: chọn chiều cao chân chữ theo kích thước chữ, khứa góc/lốc cung tròn và kỹ thuật bơm keo dán mặt mica không để lại vệt khói ố.",
            shortcutsAndSpecs = listOf(
                "Chữ cao < 30cm" to "Làm chân nổi cao 3 – 4cm",
                "Chữ cao 40 – 80cm" to "Làm chân nổi cao 5 – 8cm",
                "Chữ nóc tòa nhà > 1.5m" to "Làm chân nổi cao 12 – 15cm",
                "Keo dán mặt Mica" to "Keo lạnh Mica không khói / Keo AB (tránh 502 thường)"
            ),
            commonMistakes = listOf(
                "Mặt chữ Mica bị đục trắng mờ nhòe xung quanh đường viền: Do dùng keo 502 thường bơm quá tay làm khói keo bốc lên bám vào mặt bóng mica.",
                "Chữ có nét uốn cong bị gãy khúc gồ ghề: Do không dùng kìm vuốt cung hoặc bẻ góc gấp quá mạnh."
            ),
            practiceExercise = "Bài tập thực hành: Uốn hoàn chỉnh 1 chữ cái có cả góc vuông và cung tròn (VD chữ 'B' hoặc 'R' cao 25cm) bằng chân nhôm gờ F, dán mặt mica sữa 3mm bằng keo tàng hình sạch keo 100%.",
            steps = listOf(
                GuideStep(
                    stepNumber = 1,
                    title = "Chọn Chiều Cao Chân Chữ, Khứa Góc & Uốn Theo Đường Viền",
                    content = "Chữ nổi đẹp phụ thuộc vào độ mượt của đường uốn chân ôm sát 100% đường cắt mặt chữ Mica.",
                    detailedChecklist = listOf(
                        "Chọn bản rộng cuộn chân nhôm/inox cân đối với chiều cao chữ (3–4cm cho chữ nhỏ; 5–8cm cho chữ vừa; 10–15cm cho chữ lớn).",
                        "Đưa dải chân qua máy khứa góc (hoặc kìm khứa V) tại các điểm góc nhọn vuông.",
                        "Với các nét uốn cong tròn (chữ O, C, S, B), dùng kìm vuốt cung hoặc máy lốc 3 trục uốn từ từ theo đường biên mặt Mica đã cắt sẵn."
                    ),
                    expectedResult = "Khung chân chữ ôm khít biên dạng mặt Mica, các góc bẻ sắc nét, đường cong tròn mượt.",
                    proTip = "Dán băng keo giấy bảo vệ lên bề mặt bóng của mặt Mica trước khi uốn ghép để tránh trầy xước khi thao tác trên bàn xưởng.",
                    techNote = "Chân Inox hàn thiếc hoặc hàn laser phải mài nhẵn bavia bên trong trước khi lót tấm hậu Formex gắn LED."
                ),
                GuideStep(
                    stepNumber = 2,
                    title = "Dán Mặt Mica Tàng Hình Không Khói & Gia Cố Tấm Hậu Formex",
                    content = "Bơm keo sạch sẽ quyết định tính thẩm mỹ khi nhìn chữ ở cự ly gần trong showroom hoặc quầy lễ tân.",
                    detailedChecklist = listOf(
                        "Đặt úp mặt Mica vào rãnh gờ F của chân nhôm (hoặc canh đều viền với chân Inox), dùng kẹp chữ C / kẹp nhựa cố định tạm các góc.",
                        "Dùng kim bơm đầu siêu nhỏ bơm **keo lạnh Mica không khói** hoặc **keo AB** dọc theo khe tiếp xúc bên trong mặt chữ.",
                        "Chờ 10–15 phút cho keo chết hoàn toàn rồi mới tháo kẹp; cắt tấm hậu Formex 5–8mm lọt lòng dán LED rồi bắt vít giữ chặt vào mặt hậu."
                    ),
                    expectedResult = "Mặt Mica dính chắc chắn chịu nắng mưa, mép dán trong suốt sạch sẽ không một vết ố khói.",
                    proTip = "Bật nhẹ quạt gió thổi ngang qua chữ sau khi chấm keo giúp hơi keo bay đi ngay, ngăn hoàn toàn hiện tượng ố trắng mặt bóng.",
                    techNote = "Khoan 1–2 lỗ nhỏ phi 4mm ở cạnh đáy dưới của chân chữ lắp ngoài trời để nước mưa ngưng tụ có thể thoát ra, bảo vệ dải LED bên trong."
                )
            )
        )
    )
}
