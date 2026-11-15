package com.example.data

import com.example.model.AcademyGuide
import com.example.model.GuideStep

object AcademyCurriculumData {

    val softwareGuides: List<AcademyGuide> = listOf(
        AcademyGuide(
            id = "maintop",
            title = "Maintop 6.0 / 6.1 — Giáo Trình RIP Máy In Bạt Hiflex & Decal Từ A–Z",
            category = "Phần Mềm RIP In",
            level = "Cầm Tay Chỉ Việc",
            iconName = "print",
            durationMinutes = 45,
            prerequisites = "Máy tính đã cài Maintop 6.0/6.1 + Dongle USB (nếu dùng bản khóa cứng), đã nạp Driver & ICC Profile đúng dòng đầu phun (i3200 / Konica 512i / DX5 / Ricoh Gen5).",
            summary = "Học bài bản từ khâu tạo khổ cuộn bạt 3.2m / decal 1.52m, tính DPI không vỡ ảnh, xử lý ghép mí bạt pano khổng lồ, chọn số Pass/Feathering xóa sọc đến xuất lệnh .PRN chuẩn.",
            shortcutsAndSpecs = listOf(
                "Ctrl + N" to "Tạo khổ trang in mới (Custom Page Setup)",
                "Ctrl + I" to "Nhập file ảnh/vector (.TIF, .JPG, .EPS, .PDF)",
                "Ctrl + P" to "Mở hộp thoại cấu hình máy in & xuất file .PRN",
                "Ctrl + T" to "Chuyển đổi/xoay/chỉnh kích thước chính xác đối tượng",
                "Bạt Hiflex ngoài trời" to "72 – 100 DPI | 4 Pass | Khổ 1.8m – 3.2m",
                "Decal sữa / PP / UV" to "150 – 300 DPI | 6–8 Pass | Bật Feather Medium/High"
            ),
            commonMistakes = listOf(
                "Lỗi file RGB bị xỉn tím/đỏ cam: Quên chuyển CMYK trước khi xuất sang Maintop. Cách sửa: Quay lại Corel/Photoshop đổi sang CMYK rồi xuất lại .TIF.",
                "Lỗi mất chữ hoặc nhảy font khi mở file .EPS/.PDF: Do chưa Convert Font (Ctrl+Q ở Corel hoặc Ctrl+Shift+O ở AI). Cách sửa: Luôn convert font trước khi xuất.",
                "Lỗi đường viền mỏng/bóng đổ bị ô vuông đen: Maintop 6.0 đọc kém hiệu ứng Drop Shadow/Transparency. Cách sửa: Convert hiệu ứng thành Bitmap CMYK trước khi Export.",
                "Lỗi sọc ngang trắng đều đặn giữa các Pass: Bước tiến Step bị thừa hoặc chưa bật Feathering. Cách sửa: Bật Feather Medium/High trong Properties và chỉnh Step trên PrintExp/BYHX."
            ),
            practiceExercise = "Bài tập thực hành: Xuất một tấm bạt Hiflex kích thước 4.5m x 3.8m từ CorelDRAW sang Maintop 6.0, thực hiện cắt ghép khổ bạt 3.2m với phần chồng mí (Overlap) 25mm, bật thước đo kiểm tra kích thước rồi xuất ra file .PRN hoàn chỉnh.",
            steps = listOf(
                GuideStep(
                    stepNumber = 1,
                    title = "Thiết Lập Khổ Trang In Chuẩn Theo Cuộn Bạt & Decal Thực Tế",
                    content = "Trước khi đưa file vào in, kỹ thuật viên phải khai báo kích thước cuộn vật liệu thật đang gắn trên máy để tránh tình trạng máy in tràn ra ngoài mép bạt hoặc chạm lá thép giữ phôi.",
                    detailedChecklist = listOf(
                        "Mở Maintop -> nhấn Ctrl + N (hoặc File -> New) -> chọn tab Custom.",
                        "Đổi đơn vị đo sang Millimeter (mm) để tính chính xác từng milimét.",
                        "Nhập Width (chiều ngang khổ máy): bạt 3.2m đặt 3150mm (trừ lề an toàn hai bên 25mm); bạt 2.5m đặt 2450mm; decal 1.52m đặt 1480mm; decal 1.27m đặt 1230mm.",
                        "Nhập Height (chiều dài chạy cuộn): bằng tổng chiều dài file cần in cộng thêm tối thiểu 100–150mm biên kéo đầu/cuối.",
                        "Nhấn OK và dùng con lăn chuột kiểm tra khung giấy trắng hiển thị đúng tỉ lệ."
                    ),
                    expectedResult = "Vùng trang in hiển thị đúng khổ cuộn thực tế, hai mép an toàn không vượt quá hành trình đầu phun.",
                    proTip = "Nên lưu sẵn các mẫu Page Setup hay dùng (Bạt 3.2m, Decal 1.52m, Decal 1.27m) để ca sau mở trong 2 giây.",
                    techNote = "Không đặt Width sát đúng 3200mm nếu lá thép kẹp bạt đang kẹp mỗi bên 20mm vì mực sẽ phun dính lên thanh kẹp gây lem bạt."
                ),
                GuideStep(
                    stepNumber = 2,
                    title = "Nhập File Thiết Kế (Import) & Kiểm Soát Kích Thước, DPI, Hệ Màu",
                    content = "Chất lượng bản in quyết định 70% ở khâu chuẩn bị file đầu vào. Maintop xử lý mượt và chuẩn màu nhất với định dạng TIFF (.tif) hoặc JPEG chất lượng cao hệ màu CMYK.",
                    detailedChecklist = listOf(
                        "Nhấn Ctrl + I (hoặc File -> Import Picture) -> chọn file .TIF / .JPG / .EPS.",
                        "Kiểm tra bảng thông số thuộc tính góc dưới hoặc nhấn Ctrl + T: xác nhận lại Width x Height đúng kích thước khách đặt (ví dụ 3000mm x 1500mm).",
                        "Nếu file thiết kế xuất ở tỉ lệ 1:10 (ví dụ 300mm x 150mm), nhập hệ số phóng 1000% hoặc gõ trực tiếp 3000mm x 1500mm (nhớ khóa biểu tượng ổ khóa tỉ lệ Keep Aspect Ratio).",
                        "Kiểm tra DPI thực sau khi phóng: bạt pano nhìn xa cần >= 72 DPI; biển gần/hộp đèn >= 100 DPI; decal/tem nhãn >= 150–300 DPI."
                    ),
                    expectedResult = "File nằm gọn trong vùng trang in, kích thước đo kiểm chính xác tới từng milimét, hình không bị rạn pixel khi zoom 100%.",
                    proTip = "Nếu phải in nhiều bản giống nhau (VD 20 tấm băng rôn 0.8x3m), dùng lệnh Edit -> Step and Repeat và đặt khoảng cách hở (Gap) 15mm để thợ cắt rọc dễ dàng.",
                    techNote = "Tuyệt đối kiểm tra hệ màu phải là CMYK. File RGB nạp vào Maintop sẽ khiến màu đỏ tươi thành cam và màu xanh dương chuyển tím."
                ),
                GuideStep(
                    stepNumber = 3,
                    title = "Kỹ Thuật Chia Khổ Bạt Pano Khổng Lồ (Tiling & Overlap)",
                    content = "Khi gặp biển Pano kích thước lớn hơn khổ máy (ví dụ biển cao 4.5m x dài 10m trong khi máy in tối đa khổ 3.2m), bắt buộc phải chia mảnh (Tiling) có mí chồng để thợ thi công dán nối bằng keo chuyên dụng.",
                    detailedChecklist = listOf(
                        "Chọn đối tượng hình lớn vượt khung giấy -> mở công cụ Page/Tile Split trên thanh công cụ.",
                        "Chọn hướng chia (ngang hoặc dọc) sao cho số lượng đường nối ít nhất và nằm ở vị trí ít lộ mặt người/chữ nhất.",
                        "Thiết lập Overlap (phần in đè chồng mí) từ 20mm đến 30mm để thợ quét keo dán bạt chắc chắn không bị bung khi gặp gió bão.",
                        "Tích thêm tùy chọn in đường cắt dấu nối (Crop Mark) ở hai đầu mí ghép."
                    ),
                    expectedResult = "Bản in tự động tách thành nhiều file mảnh nối tiếp nhau, mỗi mảnh có phần hình trùng nhau đúng 20–30mm ở mép ghép.",
                    proTip = "Với biển ngang 10m x cao 4.5m, nên xoay ngang file 90 độ trên khổ bạt 3.2m để chia thành 2–3 mảnh dọc ngắn thay vì 1 đường nối dài 10m — dễ căng phẳng hơn nhiều.",
                    techNote = "Không thay đổi bất kỳ thông số màu hay độ sáng nào giữa các mảnh nối để tránh hai nửa tấm bạt bị lệch tông màu khi ghép lại."
                ),
                GuideStep(
                    stepNumber = 4,
                    title = "Chọn Driver Đầu Phun, ICC Profile, Số Pass & Chế Độ Feathering",
                    content = "Đây là bước quyết định độ đậm màu, tốc độ in và độ mịn bề mặt mực. Mỗi đầu phun và chất liệu bạt/decal có cấu hình Pass và ICC riêng.",
                    detailedChecklist = listOf(
                        "Nhấn Ctrl + P để mở hộp thoại Print -> bấm nút Properties.",
                        "Tại mục Printer Type / Model: chọn đúng Driver đầu phun của xưởng (VD: Epson i3200 Eco-Solvent, Konica 512i, Ricoh Gen5 UV).",
                        "Tại mục Paper / Media & ICC Profile: chọn Profile khớp vật liệu (Hiflex / Frontlit cho bạt; Vinyl / PP cho decal).",
                        "Chọn Pass in: Bạt Hiflex thường chọn 4 Pass; Bạt không gân/hộp đèn chọn 6 Pass; Decal sữa/PP/Tem mịn chọn 6–8 Pass.",
                        "Kích hoạt Feather / Eclosion mức Medium hoặc High để phun tơi ranh giới giữa các lượt pass, xóa sọc ngang."
                    ),
                    expectedResult = "Cửa sổ Print Preview hiển thị đúng hướng cuộn giấy, thông số Pass và ICC khớp với chất liệu lắp trên máy.",
                    proTip = "Vào ngày trời nồm ẩm, giảm bớt 5–10% mật độ mực tổng hoặc bật thêm chế độ sấy nhiệt trước 40°C / sau 45°C để tránh đọng vũng mực trên decal.",
                    techNote = "Không dùng ICC Profile của mực nước cho máy mực dầu/UV vì lượng mực xả ra khác nhau sẽ gây chảy mực."
                ),
                GuideStep(
                    stepNumber = 5,
                    title = "Xuất File .PRN Chuẩn & Kiểm Định Trước Khi Chạy Máy",
                    content = "Sau khi cấu hình hoàn tất, xuất file RIP ra định dạng `.PRN` để phần mềm điều khiển máy in (PrintExp / BYHX / WellPrint) đọc và đẩy ra đầu phun.",
                    detailedChecklist = listOf(
                        "Trong cửa sổ Print, tích chọn ô 'Print to file' -> nhấn OK.",
                        "Đặt tên file ngắn gọn, KHÔNG dấu tiếng Việt, KHÔNG khoảng trắng đặc biệt (VD: `bien_cafe_3x1m2_6pass.prn`).",
                        "Chờ thanh tiến trình RIP chạy đủ 100% và đóng hoàn toàn trước khi mở file ở phần mềm điều khiển máy in.",
                        "Kiểm tra dung lượng file .PRN (khác 0 KB), nạp vào PrintExp/BYHX và test đầu phun trước khi bấm Print."
                    ),
                    expectedResult = "Xuất thành công file `.prn` hợp lệ, đầu phun đủ tia, bản in ra đúng kích thước và màu sắc chuẩn CMYK.",
                    proTip = "Nếu cần giữ lại file thiết lập để in lại lần sau cho khách chuỗi cửa hàng, chọn File -> Save lưu thêm file `.mtp` gốc của Maintop.",
                    techNote = "Không mở hoặc di chuyển file `.prn` khi Maintop chưa báo RIP xong 100% vì sẽ gây lỗi hỏng nửa tấm hình đang in."
                )
            )
        ),
        AcademyGuide(
            id = "coreldraw",
            title = "CorelDRAW — Giáo Trình Thiết Kế & Xuất File Chuẩn Ngành Quảng Cáo",
            category = "Thiết Kế Đồ Họa",
            level = "Cầm Tay Chỉ Việc",
            iconName = "design",
            durationMinutes = 60,
            prerequisites = "Máy tính cài CorelDRAW (X7 / 2019 / 2021 / 2024 trở lên), đã cài bộ Font Việt hóa chuẩn quảng cáo (UTM, UVN, SVN, Montserrat).",
            summary = "Làm chủ CorelDRAW từ gốc: quy tắc tỉ lệ 1:10 cho biển lớn, bảng mã màu CMYK chuẩn thợ, kỹ thuật PowerClip, Convert Font/Outline, tạo đường bế tem decal và xuất .TIF / .DXF.",
            shortcutsAndSpecs = listOf(
                "Ctrl + Q" to "Convert to Curves (biến toàn bộ chữ thành nét vector)",
                "Ctrl + Shift + Q" to "Convert Outline to Object (tách viền thành khối)",
                "F12" to "Mở bảng Outline Pen (tích Scale with object để không vỡ viền)",
                "Ctrl + G / Ctrl + U" to "Group (nhóm) / Ungroup (rã nhóm) đối tượng",
                "Ctrl + K" to "Break Apart (tách rời hiệu ứng Contour / chữ ghép)",
                "Ctrl + E" to "Export xuất file sang .TIF (in) hoặc .DXF (cắt CNC)",
                "Đỏ cờ: C0 M100 Y100 K0" to "Đen tuyền in bạt: C30 M30 Y30 K100"
            ),
            commonMistakes = listOf(
                "Quên Convert to Curves (Ctrl+Q) khi gửi file sang máy in khác: Font bị nhảy về Times New Roman hoặc Arial làm hỏng hoàn toàn bố cục biển hiệu.",
                "Dùng đen K100 đơn thuần trên nền lớn: Khi in bạt Hiflex bị xám bạc, loang lổ. Phải pha Rich Black (C30 M30 Y30 K100).",
                "Để hiệu ứng Drop Shadow / Transparency rồi Export trực tiếp .EPS: Khi RIP bị hiện mảng vuông tối xung quanh bóng. Phải Convert to Bitmap CMYK trước.",
                "Chữ cắt CNC đặt quá sát nhau (< 5mm): Dao phay 3.175mm cắt đứt lẹm vào nét chữ bên cạnh. Luôn xếp chữ cách nhau tối thiểu 8–10mm."
            ),
            practiceExercise = "Bài tập thực hành: Thiết kế biển hiệu kích thước 6m x 1.5m (vẽ tỉ lệ 1:10 là 60cm x 15cm), phối màu CMYK chuẩn, làm chữ nổi có viền F12 Scale with object, bóng đổ Convert to Bitmap, tạo PowerClip nền ảnh và Export ra file .TIF CMYK đạt chuẩn 100 DPI kích thước thật.",
            steps = listOf(
                GuideStep(
                    stepNumber = 1,
                    title = "Thiết Lập Đơn Vị Đo & Công Thức Tỉ Lệ 1:1 / 1:10 Cho Biển Tấm Lớn",
                    content = "Trong ngành quảng cáo, thiết lập sai đơn vị hoặc sai tỉ lệ là lỗi nghiêm trọng nhất gây tốn hàng triệu đồng tiền bạt in hỏng. Hãy nắm chắc quy tắc 1:1 và 1:10.",
                    detailedChecklist = listOf(
                        "Nhấn Ctrl + N tạo trang mới -> tại Drawing Units chọn **Centimeters** (cm) hoặc **Millimeters** (mm). Đặt Primary Color Mode = **CMYK**.",
                        "Nếu biển <= 5 mét (VD: 3.5m x 1.2m): nhập kích thước thật 350cm x 120cm (tỉ lệ 1:1).",
                        "Nếu biển > 5 mét (VD Pano 12m x 4m): vẽ thu gọn **tỉ lệ 1:10** -> nhập 120cm x 40cm. Khi Export sang TIFF tăng DPI lên 10 lần (đặt 720–1000 DPI ở file 1:10 để khi phóng ra 12m x 4m đạt chuẩn 72–100 DPI).",
                        "Vẽ khung chữ nhật đúng kích thước biển bằng công cụ Rectangle (F6), nhấn phím `P` để đưa khung về chính giữa trang giấy."
                    ),
                    expectedResult = "Khung thiết kế chuẩn tỷ lệ, thanh thuộc tính hiển thị đúng đơn vị CMYK và kích thước tính toán được.",
                    proTip = "Dùng công thức Bleed (+4cm mỗi cạnh) cho biển bạt căng khung sắt để chữ và logo không bị kéo gập ra mặt sau khung.",
                    techNote = "Tuyệt đối không kéo giãn ảnh Bitmap không đồng đều theo một chiều vì sẽ làm méo hình người và logo khách hàng."
                ),
                GuideStep(
                    stepNumber = 2,
                    title = "Pha Bảng Màu CMYK Chuẩn Xưởng & Kỹ Thuật PowerClip Chèn Ảnh Nền",
                    content = "Màu nhìn trên màn hình (RGB) luôn rực hơn bản in thật (CMYK). Thợ thiết kế giỏi phải thuộc các công thức màu CMYK để in ra tươi sáng, đúng nhận diện thương hiệu.",
                    detailedChecklist = listOf(
                        "Vào Window -> Color Palettes -> bật **Default CMYK Palette**, tắt bảng màu RGB để tránh bấm nhầm.",
                        "Áp dụng bảng màu vàng chuẩn xưởng: Đỏ cờ (`0-100-100-0`), Đỏ đô (`0-100-100-35`), Vàng tươi (`0-15-100-0`), Xanh lá tươi (`80-0-100-0`), Xanh dương đậm (`100-70-0-0`), Đen tuyền nền (`30-30-30-100`).",
                        "Chèn ảnh nền vào khung biển: Chọn ảnh -> chuột phải -> **PowerClip Inside** -> click vào khung chữ nhật nền.",
                        "Giữ phím Ctrl + click vào khung PowerClip để chỉnh vị trí ảnh gọn đẹp, sau đó nhấn `Finish Editing`."
                    ),
                    expectedResult = "Toàn bộ chữ, nền, hình ảnh đều nằm gọn trong khung PowerClip với mã màu CMYK chuẩn xác.",
                    proTip = "Với chữ màu đen cỡ nhỏ (địa chỉ, số điện thoại), chỉ dùng `C0 M0 Y0 K100` để các đầu phun không bị lệch tia chồng màu làm nhòe nét chữ.",
                    techNote = "Tránh dùng màu pha quá 300% tổng mực (VD C100 M100 Y100 K100) vì in trên decal hoặc bạt sẽ bị chảy mực lâu khô."
                ),
                GuideStep(
                    stepNumber = 3,
                    title = "Convert Font (Ctrl+Q), Khóa Viền F12 & Xử Lý Hiệu Ứng Chống Lỗi RIP",
                    content = "Trước khi gửi file sang máy in hoặc thợ CNC, phải 'đóng băng' toàn bộ font chữ, đường viền và hiệu ứng bóng đổ để không xảy ra bất kỳ sai lệch nào.",
                    detailedChecklist = listOf(
                        "Nhấn F12 trên các chữ có viền: tích chọn cả **Behind fill** (viền nằm sau mặt chữ không ăn vào ruột chữ) và **Scale with object** (phóng to thu nhỏ viền đi theo tỉ lệ).",
                        "Vào Edit -> Select All -> Text (hoặc Ctrl + A trên cụm chữ) -> nhấn **Ctrl + Q** (Convert to Curves) để biến toàn bộ font thành vector.",
                        "Nếu cần cắt decal hoặc CNC chữ có viền dày: chọn đường viền -> nhấn **Ctrl + Shift + Q** để biến nét viền thành mảng khối độc lập.",
                        "Chọn các đối tượng có Drop Shadow / Transparency / Mesh Fill -> Bitmaps -> **Convert to Bitmap** (Color Mode: CMYK 32-bit, Resolution: 150–300 DPI, tích Transparent background)."
                    ),
                    expectedResult = "File không còn bất kỳ Text sống hay hiệu ứng Transparency chưa gộp nào; kiểm tra tại File -> Document Properties báo `No Text Objects`.",
                    proTip = "Luôn Save thành 2 bản: 1 bản gốc còn chữ sống để sửa khi khách đổi số điện thoại (`tenfile_goc.cdr`), 1 bản đã Convert (`tenfile_in.cdr`) để xuất in.",
                    techNote = "Sau khi nhấn Ctrl + Q sẽ không sửa lại nội dung chính tả được nữa, nên phải rà lỗi chính tả thật kỹ trước khi bấm Ctrl + Q."
                ),
                GuideStep(
                    stepNumber = 4,
                    title = "Tạo Đường Cắt Bế Decal (Contour) & Chuẩn Hóa File Cắt CNC / Laser",
                    content = "CorelDRAW là phần mềm mạnh nhất Việt Nam để bóc file sản xuất cho máy cắt decal, máy cắt CNC Router và Laser CO2.",
                    detailedChecklist = listOf(
                        "Làm tem bế decal: Chọn nhóm tem -> mở Effects -> Contour -> chọn Outside Contour, Steps = 1, Offset = 1.5–2mm -> nhấn Ctrl + K (Break Contour Group Apart) để tách đường cắt.",
                        "Gán cho đường cắt vừa tách nét viền Hairline và màu Spot Color `CutContour` (hoặc xuất riêng đường viền đó sang .PLT/.DXF cho máy cắt decal).",
                        "Làm file cắt CNC chữ nổi Mica/Alu: Bỏ hết màu tô (Left click dấu X trên bảng màu), chỉ để viền Hairline màu đen. Nếu chữ viết liền nét script, chọn chữ -> nhấn **Weld** để hàn liền các nét giao nhau.",
                        "Sắp xếp các chữ trên khổ tấm giả lập `1220mm x 2440mm` cách nhau >= 8–10mm và cách mép tấm >= 15mm."
                    ),
                    expectedResult = "Đường cắt bế ôm khít quanh tem không đứt đoạn; file cắt CNC sạch nét trùng và gọn gàng trong khổ tấm 1.22x2.44m.",
                    proTip = "Dùng công cụ Virtual Segment Delete hoặc Shape Tool (F10) xóa bớt các nút Node thừa trên đường cong chữ để đầu cắt Laser/CNC chạy mượt không bị giật cục.",
                    techNote = "Khi Export sang file `.DXF` cho ArtCAM/Aspire/RDWorks, chọn phiên bản AutoCAD R14 / 2004 và đơn vị Export là Millimeters."
                ),
                GuideStep(
                    stepNumber = 5,
                    title = "Export File Chuẩn (.TIF / .PDF / .DXF) Giao Cho Máy In & Máy Cắt",
                    content = "Xuất đúng định dạng cho từng công đoạn giúp giữ 100% độ sắc nét và đúng kích thước thực tế.",
                    detailedChecklist = listOf(
                        "Quét chọn đúng phần thiết kế cần in -> nhấn **Ctrl + E** (Export) -> tích chọn **Selected only**.",
                        "Xuất in bạt/decal sang Maintop: Chọn **TIFF Bitmap (.tif)** -> Compression: **LZW** -> Color Mode: **CMYK Color (32-bit)** -> nhập đúng Width/Height kích thước thật (hoặc giữ tỉ lệ 1:10 và nâng DPI tương ứng) -> tích Anti-aliasing.",
                        "Xuất in + bế tem sang PhotoPrint: Chọn **PDF** hoặc **EPS** giữ nguyên đường vector Spot Color `CutContour`.",
                        "Mở lại file ảnh `.tif` bằng phần mềm xem ảnh hoặc kéo thử vào Maintop để kiểm tra lần cuối trước khi chuyển qua máy in."
                    ),
                    expectedResult = "Xuất xong file thành phẩm sạch sẽ, đúng kích thước, đúng hệ màu CMYK và sẵn sàng chạy máy.",
                    proTip = "Nếu file quá nặng (> 500MB) khi xuất TIFF cỡ lớn, hạ DPI từ 150 xuống 100 DPI cho bạt ngoài trời để Maintop RIP nhanh gấp 3 lần mà mắt thường nhìn từ > 2m vẫn siêu nét.",
                    techNote = "Không xuất TIFF định dạng RGB vì khi sang Maintop sẽ bị lệch hoàn toàn màu sắc thương hiệu."
                )
            )
        ),
        AcademyGuide(
            id = "photoprint",
            title = "PhotoPrint / FlexiPRINT — Giáo Trình RIP Cao Cấp & In Bế Tem Decal Tự Động",
            category = "Phần Mềm RIP In",
            level = "Chuyên Gia In & Bế",
            iconName = "print",
            durationMinutes = 40,
            prerequisites = "Máy in decal/bạt kết nối LAN/USB với máy tính cài FlexiPRINT / PhotoPrint, máy cắt bế có mắt thần ARMS (Graphtec / Mimaki / Summa).",
            summary = "Thành thạo Production Manager, gán đường cắt bế CutContour kèm 4 dấu mắt thần, hiệu chỉnh màu sắc thương hiệu chuẩn xác và xếp phôi Nesting tiết kiệm tới 30% decal.",
            shortcutsAndSpecs = listOf(
                "Nesting (Chuột phải -> Nest)" to "Tự động xoay ghép nhiều file lẻ tiết kiệm khổ cuộn",
                "Spot Color CutContour" to "Tên màu bắt buộc để PhotoPrint nhận diện đường bế",
                "Perceptual Intent" to "Chế độ màu tốt nhất cho ảnh chụp người / phong cảnh",
                "Relative Colorimetric" to "Chế độ màu chuẩn nhất cho logo / chữ thương hiệu",
                "Ink Limit khuyến nghị" to "240% – 280% cho decal PVC tránh chảy mực nhăn màng"
            ),
            commonMistakes = listOf(
                "Máy cắt bế không nhận mắt thần (Mark Scan Error): Do chừa lề sau dấu mắt thần cuối quá ngắn (< 3cm) hoặc ánh đèn xưởng chiếu chói thẳng vào cảm biến quang.",
                "Cắt bế bị lệch dần về cuối cuộn: Do kẹp bánh tì không đều lực hoặc chạy cuộn decal dài > 2 mét một lần cắt. Nên chia mỗi lượt in & bế dài tối đa 1.2–1.5m.",
                "Đường bế không hiển thị trong Production Manager: Do trong file thiết kế chưa đặt đúng tên Spot Color là `CutContour` hoặc đang dùng stroke không phải vector."
            ),
            practiceExercise = "Bài tập thực hành: Nạp 3 mẫu tem nhãn khác kích thước có đường viền CutContour vào PhotoPrint, chạy lệnh Nesting trên khổ 1.27m, bật dấu mắt thần Graphtec Type 2 và kiểm tra đường cắt hiển thị đỏ đứt đoạn trên bản xem trước.",
            steps = listOf(
                GuideStep(
                    stepNumber = 1,
                    title = "Cấu Hình Máy In & Máy Cắt Trong Production Manager",
                    content = "Production Manager là trái tim điều phối hàng đợi giữa máy in và máy cắt bế của hệ thống FlexiPRINT / PhotoPrint.",
                    detailedChecklist = listOf(
                        "Mở Production Manager -> chọn Setup -> Add Setup.",
                        "Chọn hãng & model máy in thực tế, trỏ đúng cổng TCP/IP (nhập IP của máy in, VD `192.168.1.200`) hoặc USB.",
                        "Tiếp tục Add Setup thứ hai cho máy cắt bế decal (Graphtec CE7000, Mimaki CG-SRIII, Summa...) để liên kết đồng bộ mã ID bản in.",
                        "Kiểm tra trạng thái máy hiện `Ready` và thử gửi lệnh Test Cut nhẹ để xác nhận kết nối thông suốt."
                    ),
                    expectedResult = "Cả máy in và máy cắt đều hiện Ready trong danh sách Setup của Production Manager.",
                    proTip = "Cài đặt địa chỉ IP tĩnh trên card mạng máy tính (cùng lớp mạng với máy in) để không bao giờ bị mất kết nối giữa chừng khi đang RIP file nặng.",
                    techNote = "Tắt chế độ Sleep / Ngủ đông ổ cứng trên Windows của máy tính điều khiển máy in để tránh đứt kết nối khi chạy đơn hàng dài qua đêm."
                ),
                GuideStep(
                    stepNumber = 2,
                    title = "Thiết Lập Đường Cắt CutContour & Dấu Mắt Thần (Registration Marks)",
                    content = "Để in xong cuộn decal rồi chuyển qua máy cắt bế cắt chính xác từng con tem nhỏ 2–3cm mà không lệch dù chỉ 0.5mm, phải thiết lập chuẩn dấu mắt thần.",
                    detailedChecklist = listOf(
                        "Kéo file `.PDF` hoặc `.EPS` (đã có nét `CutContour`) vào hàng đợi Production Manager -> nhấp đúp mở Job Properties.",
                        "Chọn tab **Contour Cut** -> kiểm tra các đường viền màu đỏ đứt đoạn ôm đúng biên từng tem.",
                        "Tại mục Registration Marks: chọn chuẩn mắt thần trùng với máy cắt bế của xưởng (VD: `Graphtec Segment Type 2` hoặc `Mimaki Type 2`).",
                        "Nếu in trên decal gương/bóng chói, bật kích thước dấu mắt thần dày hơn (Thickness 0.8–1mm) để cảm biến quang đọc dễ dàng."
                    ),
                    expectedResult = "Bản Preview hiện đầy đủ hình in + đường cắt bế đỏ + 4 dấu góc chữ L/vuông định vị rõ nét ở bốn góc.",
                    proTip = "Với đơn hàng nhiều tem nhỏ chạy dài hơn 1.5m, chọn kiểu dấu mắt thần **Segment Area** (thêm dấu định vị trung gian mỗi 40–50cm) để bù sai số trượt giấy.",
                    techNote = "Khi lắp cuộn decal vào máy cắt bế, luôn kéo chừa dư đuôi giấy tối thiểu 4cm sau dấu mắt thần cuối để bánh tì không bị rơi khỏi cuộn."
                ),
                GuideStep(
                    stepNumber = 3,
                    title = "Quản Lý Màu ICC, Mức Phun Mực (Ink Limit) & Xếp Phôi Nesting",
                    content = "Điều chỉnh đúng ICC Profile và sắp xếp phôi thông minh giúp màu sắc đẹp như màn hình thiết kế và tiết kiệm đáng kể vật liệu cuộn.",
                    detailedChecklist = listOf(
                        "Mở tab **Color Management** -> chọn ICC Profile đúng chất liệu đang in (Vinyl Glossy / Matte / Backlit Film).",
                        "Chọn Rendering Intent: **Perceptual** cho ảnh chụp/chân dung; **Relative Colorimetric** cho logo nhận diện thương hiệu.",
                        "Trong Color Adjustment -> giữ tổng lượng mực (Total Ink Limit) khoảng 240–280% cho decal PVC để mực khô nhanh và không làm nhăn mép khi bóc dán.",
                        "Chọn nhiều file tem đang chờ trong hàng đợi -> chuột phải -> **Nesting** để tự động ghép khít tiết kiệm tối đa khổ cuộn."
                    ),
                    expectedResult = "Các file được sắp xếp gọn khít trên khổ cuộn 1.27m/1.52m với màu sắc trung thực, lượng mực cân bằng.",
                    proTip = "Bật thêm tính năng **Color Bar** ở lề trái/phải để quan sát trực quan 4 dải CMYK trong suốt quá trình in cuộn dài.",
                    techNote = "Sau khi in decal mực dầu, nên đợi tối thiểu 4–6 tiếng cho bay hơi hết dung môi rồi mới cán màng và đưa lên máy cắt bế, tránh màng cán bị bong mép."
                )
            )
        ),
        AcademyGuide(
            id = "printexp_byhx",
            title = "PrintExp / BYHX / WellPrint — Làm Chủ Phần Mềm Điều Khiển Máy In KTS",
            category = "Điều Khiển Máy In",
            level = "Thợ Đứng Máy Chính",
            iconName = "print",
            durationMinutes = 40,
            prerequisites = "Máy in bạt/decal đã bật nguồn, hệ thống bình mực phụ đầy mực, nhiệt độ sấy bàn in đạt mức ổn định.",
            summary = "Nắm trọn quy trình đứng máy thực tế: lấy gốc Origin, test đầu phun, kiểm soát áp suất âm, hiệu chỉnh Step xóa sạch sọc trắng/đen và cân 2 chiều Bi-direction nét căng.",
            shortcutsAndSpecs = listOf(
                "Độ cao đầu phun Decal" to "1.5mm (cách mặt decal)",
                "Độ cao đầu phun Bạt" to "2.0 – 2.5mm (tránh gân bạt quệt đầu phun)",
                "Áp suất âm (Konica/Ricoh)" to "Ổn định từ -3.2 đến -4.2 kPa",
                "Nhiệt độ sấy chuẩn Decal" to "Pre-heat 38–40°C | Print 40°C | Post 45°C",
                "Sọc trắng ngang -> Giảm Step" to "Sọc đen chồng mực -> Tăng Step"
            ),
            commonMistakes = listOf(
                "Bấm Clean đầu phun quá nhiều lần liên tục: Làm cạn bình phụ, tràn mực thải và dễ gây bọt khí nghẹt đầu phun. Mỗi lần Clean nên chờ 30–60s rồi test.",
                "Chạy bạt dày 3M/không gân mà vẫn dùng thông số Step của bạt mỏng: Bạt nặng kéo ít hơn gây sọc đen đè nét. Phải lưu Step riêng cho từng loại bạt.",
                "Tắt aptomat tổng khi đầu phun chưa đóng kín vào bộ Cap giữ ẩm: Qua một đêm mặt đầu phun bị khô mực chết tia vĩnh viễn."
            ),
            practiceExercise = "Bài tập thực hành: Thực hiện đúng trình tự bật máy -> nâng nhiệt sấy -> chạy Nozzle Test đọc đủ vạch màu -> thực hiện Step Calibration tinh chỉnh sai số xuống 0 -> kiểm tra Bi-dir trên phần mềm PrintExp/BYHX.",
            steps = listOf(
                GuideStep(
                    stepNumber = 1,
                    title = "Nạp Cuộn Vật Liệu, Căn Độ Cao Đầu Phun & Lấy Gốc Origin",
                    content = "Thao tác nạp phôi chuẩn ngay từ đầu giúp cuộn bạt hoặc decal chạy hàng chục mét mà không bị lệch xéo sang một bên.",
                    detailedChecklist = listOf(
                        "Xả phẳng cuộn bạt/decal qua các bánh tì cao su, gạt hết các cần ép bánh tì xuống đều lực, kẹp nhẹ hai lá thép chặn mép.",
                        "Bật quạt hút chân không (Vacuum) mức trung bình với bạt, mức cao với decal mỏng để hút phẳng mặt bàn in.",
                        "Chỉnh độ cao gầm xe đầu phun: Decal đặt khe hở 1.5mm; Bạt Hiflex/bạt gân đặt 2.0–2.5mm.",
                        "Bấm phím mũi tên đưa xe đầu phun tới điểm mép phải mong muốn -> bấm **Set Origin** (hoặc X0) và đặt giá trị lề trái Margin an toàn (thường 20–30mm)."
                    ),
                    expectedResult = "Vật liệu phẳng lì trên mặt bàn nhiệt, đầu xe nằm đúng vị trí mép bắt đầu in và cách mặt vật liệu khoảng cách an toàn.",
                    proTip = "Trước khi nhấn in tấm dài, bấm nút **Feed** cho chạy thử tới 50cm để quan sát xem cuộn vật liệu có bị chạy xiên mép hay không.",
                    techNote = "Nếu phát hiện mép bạt bị quăn vểnh lên dù chỉ 2mm, tuyệt đối dừng máy vuốt phẳng lại ngay; quẹt đầu phun i3200/Konica một lần có thể hỏng ngay đầu phun."
                ),
                GuideStep(
                    stepNumber = 2,
                    title = "Đọc Bảng Test Đầu Phun (Nozzle Check) & Bảo Dưỡng Wiper / Cap",
                    content = "Không bao giờ in đơn hàng khi chưa kiểm tra bảng tia màu. Đọc đúng bảng test giúp xử lý kịp thời trước khi hỏng cả tấm bạt lớn.",
                    detailedChecklist = listOf(
                        "Bấm nút **Test / Nozzle** -> quan sát các vạch mảnh của 4 màu C-M-Y-K xem có bị đứt đoạn, khuyết tia hay xiên tia (bắn chéo) không.",
                        "Nếu thiếu vài tia nhỏ: chọn **Normal Clean**, chờ 30 giây rồi Test lại.",
                        "Nếu thiếu cả mảng lớn hoặc nghẹt nặng: dùng **Strong Clean / Purge**, lấy tăm bông thấm dung dịch rửa lau sạch gạt cao su Wiper và vành cao su cốc hút Cap Top.",
                        "Với đầu phun công nghiệp (Konica, Ricoh, Polaris): liếc đồng hồ đo áp suất âm (Negative Pressure) bảo đảm ổn định mức -3.5 kPa."
                    ),
                    expectedResult = "Bảng Nozzle Check hiện đầy đủ 100% các vạch nhỏ thẳng hàng, sắc nét cho cả 4 kênh C-M-Y-K.",
                    proTip = "Nếu thiếu chỉ 1–2 tia đơn lẻ trên đầu i3200 sau 2 lần Clean nhẹ, có thể bật tính năng bù tia (**Nozzle Compensation / Feather**) và chạy 6–8 Pass thay vì ép Purge mạnh tốn mực.",
                    techNote = "Không dùng giấy ăn hay vải bông thường lau trực tiếp lên bề mặt kim loại lỗ phun vì sợi bông và ma sát sẽ làm xước màng đầu phun."
                ),
                GuideStep(
                    stepNumber = 3,
                    title = "Hiệu Chỉnh Bước Tiến Step & Hai Chiều Bi-Direction Xóa Sọc, Nét Chữ Căng",
                    content = "Đây là kỹ năng phân biệt thợ giỏi với thợ mới: biết nhìn sọc để biết cần tăng hay giảm Step ngay trong lúc máy đang chạy.",
                    detailedChecklist = listOf(
                        "Mở tab **Calibration -> Step Calibration**: in bảng vạch chuẩn bước tiến. Tìm vạch có hai đường ngang khít nhau thành 1 đường liền duy nhất để nhập hệ số chuẩn.",
                        "Quy tắc nhìn mắt thường khi đang in thật: Thấy **sọc trắng hở** giữa hai lượt pass = máy kéo quá dư -> **GIẢM** Step (-0.2 đến -0.5). Thấy **sọc đen đậm** đè mực = máy kéo thiếu -> **TĂNG** Step (+0.2 đến +0.5).",
                        "Mở **Bi-Direction Calibration**: chạy in thử thước đo số âm/dương (`-5 ... 0 ... +5`). Chọn số mà hai vạch trên-dưới trùng khít thẳng tắp thành 1 vạch dọc rồi lưu vào máy.",
                        "Khi in tem chữ siêu nhỏ (< 2mm): chuyển sang chế độ **Uni-Direction (1 chiều)** để nét chữ sắc như in offset."
                    ),
                    expectedResult = "Mảng màu bệt (như xanh dương, đỏ) in ra mịn màng đồng nhất, không có sọc ngang; chữ nhỏ rõ nét không bị bóng đôi.",
                    proTip = "Ghi chú lại bảng thông số Step chuẩn của từng loại vật liệu (Bạt 0.36, Bạt không gân, Decal sữa) dán ngay cạnh màn hình máy tính xưởng.",
                    techNote = "Luôn khóa đầu phun về vị trí Home (Cap Top) và kiểm tra bình xả mực thải không bị đầy trước khi tắt máy cuối ngày."
                )
            )
        ),
        AcademyGuide(
            id = "riin_uv",
            title = "RIIN / Ultraprint / Flexi UV — Giáo Trình Điều Khiển Máy In UV Cuộn & UV Phẳng",
            category = "Phần Mềm RIP In",
            level = "Công Nghệ UV Cao Cấp",
            iconName = "uv",
            durationMinutes = 40,
            prerequisites = "Máy in UV cuộn 3M hoặc UV phẳng khổ lớn, mực UV chính hãng (C-M-Y-K-White-Varnish), kính bảo hộ chống tia UV.",
            summary = "Làm chủ kỹ thuật in UV cao cấp: tách lớp lót mực trắng (White Ink) cho bạt 3M hộp đèn, decal trong 2 mặt, phủ bóng 3D (Varnish) và kiểm soát nhiệt đèn LED UV.",
            shortcutsAndSpecs = listOf(
                "Color -> White" to "In trên Mica trong / kính nhìn từ mặt trước",
                "White -> Color" to "In trên Mica / Decal trong dán mặt sau kính nhìn xuyên qua",
                "Color - White - Color" to "In 3 lớp 2 mặt sắc nét trên decal trong / bạt xuyên sáng",
                "Varnish (V1 / Spot2)" to "Phủ bóng UV tạo chữ nổi 3D sang trọng trên bảng hiệu",
                "Đèn LED UV (Formex/Decal)" to "Giảm công suất 50–65% chống cong vênh vật liệu"
            ),
            commonMistakes = listOf(
                "Mực trắng White bị tắc hoặc nhạt: Do quên bật hệ thống khuấy bình mực trắng (White Stirring) hàng ngày khiến hạt titan lắng xuống đáy bình.",
                "Mực UV in lên Mica/Inox bị bong tróc khi cào móng tay: Quên lau sạch bụi dầu hoặc quên quét lớp lót tăng bám dính (Coating/Primer) cho kính và kim loại.",
                "Bị co rút nhăn tấm Formex/Decal khi chạy qua đầu in: Đặt công suất đèn sấy LED UV quá nóng (100%). Cần hạ xuống 50–65%."
            ),
            practiceExercise = "Bài tập thực hành: Thiết lập 1 file hộp đèn bạt 3M hoặc tem Mica trong gồm 3 lớp Color + White + Varnish, nạp vào RIIN/Ultraprint kiểm tra đúng thứ tự phun từng kênh và cấu hình công suất đèn LED UV phù hợp.",
            steps = listOf(
                GuideStep(
                    stepNumber = 1,
                    title = "Tách Kênh Mực Trắng (White Ink) Cho Bạt 3M, Decal Trong & Mica",
                    content = "Sức mạnh lớn nhất của in UV là có mực Trắng làm nền cản sáng/nâng tông màu khi in lên các vật liệu trong suốt hoặc tối màu.",
                    detailedChecklist = listOf(
                        "Trong Corel/AI, tạo bản sao các vùng cần lót trắng trên Layer riêng và tô bằng màu Spot Color chuẩn của phần mềm RIP (thường là `W1` hoặc `Spot1`).",
                        "Trong RIIN / Ultraprint -> mở **Spot Color / White Ink Setup** -> chọn chế độ ánh xạ kênh White từ Spot Color vừa tạo (hoặc chọn `Fill Image` lót trắng toàn vùng có mực).",
                        "Chọn thứ tự phun lớp: In mặt trước tấm Mica/Alu đen -> **White trước, Color sau**. In dán ngược sau mặt kính -> **Color trước, White sau**. In decal trong nhìn cả 2 mặt -> **Color – White – Color (3 lớp)**.",
                        "Co biên mực trắng (Choke) vào trong 1–2 pixel để viền trắng không bị lòi mép trắng ra ngoài nét màu."
                    ),
                    expectedResult = "Màu in trên vật liệu trong suốt hoặc nền tối lên rực rỡ, đúng màu gốc, không bị lộ viền mực trắng thừa.",
                    proTip = "Khi in bạt 3M hộp đèn cao cấp (ngân hàng, showroom), chọn chế độ in 2 lớp mực màu (**Double Strike / High Density**) để khi bật đèn LED ban đêm màu không bị nhạt bệch.",
                    techNote = "Luôn bật chế độ tuần hoàn & khuấy mực trắng mỗi ngày kể cả khi không có đơn hàng in mực trắng để bảo vệ đầu phun trắng."
                ),
                GuideStep(
                    stepNumber = 2,
                    title = "Phủ Bóng Định Hình Varnish 3D & Điều Chỉnh Đèn Sấy LED UV",
                    content = "Kết hợp phủ bóng Varnish và điều chỉnh đúng công suất đèn UV giúp tạo sản phẩm cao cấp mà không làm hỏng phôi nhựa nhạy nhiệt.",
                    detailedChecklist = listOf(
                        "Gán Spot Color `V1` / `Varnish` cho vùng logo hoặc họa tiết cần tạo độ bóng nổi 3D.",
                        "Trong phần mềm điều khiển UV, kích hoạt kênh Varnish (chọn Glossy bóng hoặc Matte mờ tùy đèn đóng rắn) và tăng số lượt phủ trắng nền nếu muốn tạo hiệu ứng chữ nổi sờ thấy gờ.",
                        "Cài đặt công suất đèn LED UV trái/phải: In gỗ/Alu/Kính đặt 80–100%; In Decal sữa/PP/Formex mỏng đặt **50–65%** để tránh nhiệt đèn làm phồng cong phôi chạm đầu phun.",
                        "Chạy test độ bám dính bằng băng keo dính lên góc phôi thử nghiệm sau khi sấy xong."
                    ),
                    expectedResult = "Bề mặt mực khô tức thì ngay khi ra khỏi đèn UV, logo có hiệu ứng bóng nổi 3D bắt mắt, phôi phẳng không cong vênh.",
                    proTip = "Với tấm Mica hoặc Inox trơn láng, lau nhẹ bằng dung dịch Coating tăng bám dính chuyên dụng trước khi đặt lên bàn máy UV phẳng.",
                    techNote = "Tuyệt đối che chắn ánh nắng mặt trời trực tiếp vào buồng đầu phun máy UV vì tia UV tự nhiên có thể làm đông mực chết đầu phun ngay lập tức."
                )
            )
        )
    )
}
