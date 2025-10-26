USE QL_NhaHangCrabKing_Nhom02;
GO

-- =============== HANG KHÁCH HÀNG ===============
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
('NV0006', N'Nguyễn Hà Nhật Khanh', '0911002233', 0, 1, '2024-12-01', 1, '123456'),
('NV0007', N'Trần Lê Khoa', '0909123123', 1, 1, '2025-01-05', 1, '123456'),
('NV0003', N'Đỗ Minh Quân', '0977333555', 1, 0, '2025-02-20', 1, '123456'),
('NV0004', N'Lê Hồng Nhung', '0967222333', 0, 0, '2025-03-12', 1, '123456'),
('NV0005', N'Phạm Gia Huy', '0915666777', 1, 0, '2025-04-01', 0, '123456');

-- =============== LOẠI BÀN ===============
INSERT INTO LoaiBan VALUES
('LB0001', N'A', 2),
('LB0002', N'B', 4),
('LB0003', N'C', 8),
('LB0004', N'D', 12),
('LB0005', N'E', 20);

-- =============== KHU VỰC ===============
INSERT INTO KhuVuc VALUES
('KV0001', N'Indoor'),
('KV0002', N'Outdoor'),
('KV0003', N'VIP');
-- =============== BÀN ===============
INSERT INTO Ban VALUES
('BO0001', 1, 'LB0001', 'KV0002'),
('BI0002', 1, 'LB0002', 'KV0001'),
('BV0003', 0, 'LB0004', 'KV0003'),
('BO0004', 1, 'LB0003', 'KV0002'),
('BI0005', 0, 'LB0005', 'KV0001');

-- =============== LOẠI MÓN ===============
INSERT INTO LoaiMon VALUES
('LM0001', N'Món khai vị', N'Món ăn nhẹ dùng trước bữa chính'),
('LM0002', N'Món chính', N'Các món hải sản cao cấp'),
('LM0003', N'Tráng miệng', N'Kem, bánh, chè...'),
('LM0004', N'Nước uống', N'Nước ngọt, sinh tố, rượu vang'),
('LM0005', N'Món đặc biệt', N'Món signature của nhà hàng');

-- =============== PHẦN TRĂM GIÁ BÁN ===============
INSERT INTO PhanTramGiaBan VALUES
('PG0001', 'LM0001', 30, '2025-01-01'),
('PG0002', 'LM0002', 40, '2025-01-01'),
('PG0003', 'LM0003', 25, '2025-02-01'),
('PG0004', 'LM0004', 20, '2025-03-01'),
('PG0005', 'LM0005', 50, '2025-03-01');

-- =============== KHUYẾN MÃI ===============
INSERT INTO KhuyenMai (maKM, tenKM, soLuong, ngayPhatHanh, ngayKetThuc, maThayThe, phanTramGiamGia, uuDai)
VALUES
    ('KM001', N'Giảm giá khai trương', 100, '2025-01-01', '2025-01-31', NULL, 20, 1),
    ('KM002', N'Tháng sinh nhật khách hàng', 50, '2025-02-01', '2025-02-28', 'KM001', 15, 1),
    ('KM003', N'Giảm giá ngày lễ tình nhân', 80, '2025-02-10', '2025-02-20', NULL, 10, 0),
    ('KM004', N'Khuyến mãi cuối tuần', 200, '2025-03-01', '2025-03-31', NULL, 5, 0),
    ('KM005', N'Giảm giá hè sôi động', 150, '2025-06-01', '2025-06-30', 'KM004', 25, 1);

-- =============== MÓN ===============
INSERT INTO Mon VALUES
('MN0001', N'Gỏi cua lột', N'Món khai vị hương vị thanh mát', 'goi_cua.jpg', 80000, 104000, 'LM0001', 'PG0001', 'KM0001'),
('MN0002', N'Cua hoàng đế hấp bia', N'Món đặc sản cao cấp', 'cua_hoang_de.jpg', 1200000, 1680000, 'LM0002', 'PG0002', 'KM0003'),
('MN0003', N'Chè hạt sen', N'Món tráng miệng mát lạnh', 'che_hat_sen.jpg', 40000, 50000, 'LM0003', 'PG0003', NULL),
('MN0004', N'Sinh tố xoài', N'Nước uống tươi mát', 'sinh_to_xoai.jpg', 30000, 36000, 'LM0004', 'PG0004', 'KM0002'),
('MN0005', N'Lẩu cua đồng', N'Món đặc biệt cho nhóm đông', 'lau_cua_dong.jpg', 250000, 375000, 'LM0005', 'PG0005', NULL);

-- =============== SỰ KIỆN ===============
INSERT INTO SuKien VALUES
('SK0001', N'Sinh nhật', N'Trọn gói sinh nhật nhỏ', 200000),
('SK0002', N'Tiệc kỷ niệm', N'Tổ chức kỷ niệm công ty', 300000),
('SK0003', N'Đám cưới', N'Dịch vụ tiệc cưới sang trọng', 500000),
('SK0004', N'Họp mặt bạn bè', N'Không gian riêng tư', 150000),
('SK0005', N'Tiệc Giáng Sinh', N'Sự kiện cuối năm', 250000);

-- =============== THỜI GIAN ĐỔI BÀN ===============
INSERT INTO ThoiGianDoiBan VALUES
('TD0001', 1, 30),
('TD0002', 0, 15),
('TD0003', 1, 60),
('TD0004', 0, 10),
('TD0005', 1, 45);

-- =============== CỌC ===============
INSERT INTO Coc VALUES
('CO0001', 1, 10, 0),
('CO0002', 1, 20, 0),
('CO0003', 0, 0, 200000),
('CO0004', 0, 0, 500000),
('CO0005', 1, 30, 0);

-- =============== HÓA ĐƠN ===============
INSERT INTO HoaDon VALUES
('HD12510250001', 'KH0001', 'NV0002', 'BO0001', 'CO0001', 'TD0001', 'KM0001', 'SK0001', '2025-10-20', '2025-10-20', 1, 1, 10, 80000, 500000, 450000, 25000),
('HD22510250002', 'KH0002', 'NV0003', 'BV0003', 'CO0002', 'TD0003', 'KM0002', 'SK0002', '2025-10-21', '2025-10-21', 0, 0, 10, 120000, 1000000, 850000, 150000),
('HD32510250003', 'KH0003', 'NV0004', 'BO0002', 'CO0003', 'TD0002', 'KM0003', 'SK0003', '2025-10-22', '2025-10-22', 1, 1, 10, 200000, 1200000, 1000000, 200000),
('HD42510250004', 'KH0004', 'NV0002', 'BU0005', 'CO0004', 'TD0004', NULL, 'SK0004', '2025-10-23', '2025-10-23', 0, 0, 10, 500000, 2500000, 2500000, 0),
('HD52510250005', 'KH0005', 'NV0001', 'BO0004', 'CO0005', 'TD0005', 'KM0005', 'SK0005', '2025-10-24', '2025-10-24', 1, 1, 10, 300000, 3500000, 3100000, 400000);

-- =============== CHI TIẾT HÓA ĐƠN ===============
INSERT INTO ChiTietHoaDon VALUES
('HD12510250001', 'MN0001', 2, 208000),
('HD22510250002', 'MN0002', 1, 1680000),
('HD32510250003', 'MN0004', 3, 108000),
('HD42510250004', 'MN0005', 2, 750000),
('HD52510250005', 'MN0003', 4, 200000);
GO
