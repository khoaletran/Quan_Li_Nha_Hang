USE QL_NhaHangCrabKing_Nhom02;
GO

-- =============== HẠNG KHÁCH HÀNG ===============
INSERT INTO HangKhachHang VALUES
('HH0001', 0, 0, N'Hạng Đồng - Mới tham gia'),
('HH0002', 200, 5, N'Hạng Bạc - Khách thân thiết'),
('HH0003', 500, 10, N'Hạng Vàng - Khách VIP nhỏ'),
('HH0004', 1000, 15, N'Hạng Bạch Kim - Khách VIP lớn'),
('HH0005', 2000, 20, N'Hạng Kim Cương - Khách siêu VIP');

-- =============== KHÁCH HÀNG ===============
INSERT INTO KhachHang VALUES
('KH0001', 'HH0001', N'Lê Minh Tuấn', '0912345678', 1, 120),
('KH0002', 'HH0002', N'Nguyễn Thị Hoa', '0987654321', 0, 350),
('KH0003', 'HH0003', N'Trần Quốc Nhã', '0905123456', 1, 700),
('KH0004', 'HH0004', N'Đỗ Minh Quân', '0977333444', 1, 1100),
('KH0005', 'HH0005', N'Trần Lê Khoa', '0933444555', 1, 2100);

-- =============== NHÂN VIÊN ===============
INSERT INTO NhanVien VALUES
('NV0001', N'Nguyễn Hà Nhật Khanh', '0911002233', 0, 1, '2024-12-01', 1, '1'),
('NV0002', N'Trần Lê Khoa', '0909123123', 1, 1, '2025-01-05', 1, '1'),
('NV0003', N'Đỗ Minh Quân', '0977333555', 1, 0, '2025-02-20', 1, '1'),
('NV0004', N'Lê Hồng Nhung', '0967222333', 0, 0, '2025-03-12', 1, '1'),
('NV0005', N'Phạm Gia Huy', '0915666777', 1, 0, '2025-04-01', 1, '1');

-- =============== KHU VỰC ===============
INSERT INTO KhuVuc VALUES
('KV0001', N'Outdoor'),
('KV0002', N'Indoor'),
('KV0003', N'VIP');

-- =============== LOẠI BÀN ===============
INSERT INTO LoaiBan VALUES
('LB0001', N'Loại A', 2),
('LB0002', N'Loại B', 4),
('LB0003', N'Loại C', 8),
('LB0004', N'Loại D', 12),
('LB0005', N'Loại E', 20);

-- =============== LOẠI MÓN ===============
INSERT INTO LoaiMon VALUES
('LM0001', N'Món khai vị', N'Món ăn nhẹ dùng trước bữa chính'),
('LM0002', N'Món chính', N'Món hải sản đặc trưng của nhà hàng'),
('LM0003', N'Món tráng miệng', N'Kem, chè, bánh ngọt...'),
('LM0004', N'Nước uống', N'Nước ngọt, sinh tố, bia rượu...'),
('LM0005', N'Món đặc biệt', N'Món signature - cua hoàng đế, lẩu cua, tôm hùm...');

-- =============== KHUYẾN MÃI ===============
INSERT INTO KhuyenMai (maKM, tenKM, soLuong, ngayPhatHanh, ngayKetThuc, maThayThe, phanTramGiamGia, uuDai)
VALUES
('KM0001', N'Giảm giá khai trương', 100, '2025-01-01', '2025-01-31', NULL, 20, 1),
('KM0002', N'Sinh nhật khách hàng', 50, '2025-02-01', '2025-02-28', NULL, 15, 1),
('KM0003', N'Giảm giá lễ tình nhân', 80, '2025-02-10', '2025-02-20', NULL, 10, 0),
('KM0004', N'Khuyến mãi cuối tuần', 200, '2025-03-01', '2025-03-31', NULL, 5, 0),
('KM0005', N'Giảm giá hè sôi động', 150, '2025-06-01', '2025-06-30', NULL, 25, 1);

-- =============== MÓN ĂN ===============
INSERT INTO Mon VALUES
('MN0001', N'Gỏi cua lột', N'Món khai vị tươi mát, vị chua ngọt', 'goi_cua_lot.jpg', 80000, 20, 'LM0001'),
('MN0002', N'Cua hoàng đế hấp bia', N'Món chính sang trọng, thịt ngọt', 'cua_hoang_de.jpg', 1200000, 10, 'LM0002'),
('MN0003', N'Chè hạt sen', N'Món tráng miệng thanh mát', 'che_hat_sen.jpg', 40000, 25, 'LM0003'),
('MN0004', N'Sinh tố xoài', N'Nước uống trái cây tự nhiên', 'sinh_to_xoai.jpg', 30000, 40, 'LM0004'),
('MN0005', N'Lẩu cua đồng', N'Món đặc biệt cho nhóm đông người', 'lau_cua_dong.jpg', 250000, 15, 'LM0005'),
('MN0006', N'Cơm chiên hải sản', N'Món chính cho nhóm đông', 'com_chien_hai_san.jpg', 90000, 30, 'LM0002'),
('MN0007', N'Tôm hùm nướng bơ tỏi', N'Món đặc biệt cao cấp', 'tom_hum_bo_toi.jpg', 950000, 8, 'LM0005');

-- =============== PHẦN TRĂM GIÁ BÁN ===============
INSERT INTO PhanTramGiaBan VALUES
('PG0001', 'LM0001', 'MN0001', 35, '2025-04-01'),
('PG0002', 'LM0002', 'MN0002', 45, '2025-04-01'),
('PG0003', 'LM0003', 'MN0003', 25, '2025-04-01'),
('PG0004', 'LM0004', 'MN0004', 20, '2025-04-01'),
('PG0005', 'LM0005', 'MN0005', 50, '2025-04-01'),
('PG0006', 'LM0002', 'MN0006', 40, '2025-04-01'),
('PG0007', 'LM0005', 'MN0007', 55, '2025-04-01');

-- =============== SỰ KIỆN ===============
INSERT INTO SuKien VALUES
('SK0001', N'Sinh nhật', N'Gói trang trí & nhạc sinh nhật', 200000),
('SK0002', N'Tiệc kỷ niệm', N'Không gian riêng tư cho công ty', 300000),
('SK0003', N'Đám cưới', N'Dịch vụ tiệc cưới sang trọng', 500000),
('SK0004', N'Họp mặt bạn bè', N'Không gian ấm cúng, nhẹ nhàng', 150000),
('SK0005', N'Tiệc Giáng Sinh', N'Sự kiện đặc biệt cuối năm', 250000);

-- =============== THỜI GIAN ĐỔI BÀN ===============
INSERT INTO ThoiGianDoiBan VALUES
('TD0001', 1, 15),  -- Đặt ăn liền: cho phép đổi trong 15 phút
('TD0002', 0, 5);   -- Đặt trước: cho phép đổi trong 5 phút

-- =============== CỌC ===============

INSERT INTO Coc VALUES
-- KV0001 - Outdoor
('CO0001', 1, 10,  NULL, 'LB0001', 'KV0001'),
('CO0002', 1, 15,  NULL, 'LB0002', 'KV0001'),
('CO0003', 0, NULL, 200000, 'LB0003', 'KV0001'),
('CO0004', 0, NULL, 300000, 'LB0004', 'KV0001'),

-- KV0002 - Indoor
('CO0005', 1, 20,  NULL, 'LB0001', 'KV0002'),
('CO0006', 1, 10,  NULL, 'LB0002', 'KV0002'),
('CO0007', 0, NULL, 250000, 'LB0003', 'KV0002'),
('CO0008', 0, NULL, 400000, 'LB0004', 'KV0002'),

-- KV0003 - VIP
('CO0009', 1, 15,  NULL, 'LB0004', 'KV0003'),
('CO0010', 0, NULL, 500000, 'LB0005', 'KV0003');
GO

INSERT INTO PhieuKetCa (maPhieu, maNV, ca, soHoaDon, tienMat, tienCK, tienChenhLech, ngayKetCa,moTa)
VALUES
('PK0001', 'NV0001', 0, 25,  15000000, 5000000,  0,       '2025-10-25 12:00','khách làm vỡ 2 ly'),  -- Ca sáng NV0001
('PK0002', 'NV0002', 1, 30,  18000000, 7000000,  50000,   '2025-10-25 22:00','Trẻ em làm hư cây cảnh'),  -- Ca tối NV0002
('PK0003', 'NV0003', 0, 28,  15500000, 4500000, -100000,  '2025-10-26 12:00',null),  -- Ca sáng NV0003
('PK0004', 'NV0004', 1, 35,  20000000, 8000000,  0,       '2025-10-26 22:00',null),  -- Ca tối NV0004
('PK0005', 'NV0005', 0, 20,  12000000, 4000000,  20000,   '2025-10-27 12:00',null);  -- Ca sáng NV0005
GO
