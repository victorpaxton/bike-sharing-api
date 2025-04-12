import requests
import json
import random
import time

# API endpoint and authentication
API_URL = "http://localhost:8080/api/stations"
TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpc3MiOiJodHRwczovL21ldHJvd2hlZWwub3JnIiwiaWF0IjoxNzQ0NDUwNDM3LCJleHAiOjE3NDQ1MzY4MzcsInVzZXJJZCI6ImFlMDAxZmQwLWJlZTEtNDhlMi04ZDAzLTJjNmQ2NjYzYmQzNCIsImVtYWlsIjoiYWRtaW5AZ21haWwuY29tIiwiZnVsbE5hbWUiOiJBZG1pbiBBY2NvdW50Iiwicm9sZXMiOiJBRE1JTiJ9.6ZufP32hfqgTOnF7n7jUXoUjc5FOv1aws6kRj-aKaiJ8KUSfvrDsCsiKOR-7lmeAuY2o9Uy0nfTASbY2385zPQ"
HEADERS = {
    "Authorization": f"Bearer {TOKEN}",
    "Content-Type": "application/json"
}

# Districts in Ho Chi Minh City
hcmc_districts = [
    {"name": "District 1", "wards": ["Ben Nghe", "Ben Thanh", "Cau Kho", "Cau Ong Lanh", "Co Giang", "Da Kao", "Nguyen Cu Trinh", "Nguyen Thai Binh", "Pham Ngu Lao", "Tan Dinh"]},
    {"name": "District 2", "wards": ["An Khanh", "An Loi Dong", "An Phu", "Binh An", "Binh Khanh", "Binh Trung Dong", "Binh Trung Tay", "Cat Lai", "Thanh My Loi", "Thao Dien", "Thu Thiem"]},
    {"name": "District 3", "wards": ["Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5", "Ward 6", "Ward 7", "Ward 8", "Ward 9", "Ward 10", "Ward 11", "Ward 12", "Ward 13", "Ward 14"]},
    {"name": "District 4", "wards": ["Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5", "Ward 6", "Ward 8", "Ward 9", "Ward 10", "Ward 13", "Ward 14", "Ward 15", "Ward 16", "Ward 18"]},
    {"name": "District 5", "wards": ["Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5", "Ward 6", "Ward 7", "Ward 8", "Ward 9", "Ward 10", "Ward 11", "Ward 12", "Ward 13", "Ward 14", "Ward 15"]},
    {"name": "District 6", "wards": ["Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5", "Ward 6", "Ward 7", "Ward 8", "Ward 9", "Ward 10", "Ward 11", "Ward 12", "Ward 13", "Ward 14"]},
    {"name": "District 7", "wards": ["Binh Thuan", "Phu My", "Phu Thuan", "Tan Hung", "Tan Kieng", "Tan Phong", "Tan Phu", "Tan Quy", "Tan Thuan Dong", "Tan Thuan Tay"]},
    {"name": "District 8", "wards": ["Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5", "Ward 6", "Ward 7", "Ward 8", "Ward 9", "Ward 10", "Ward 11", "Ward 12", "Ward 13", "Ward 14", "Ward 15", "Ward 16"]},
    {"name": "District 9", "wards": ["Hiep Phu", "Long Binh", "Long Phuoc", "Long Thanh My", "Long Truong", "Phuoc Binh", "Phuoc Long A", "Phuoc Long B", "Tang Nhon Phu A", "Tang Nhon Phu B", "Truong Thanh"]},
    {"name": "District 10", "wards": ["Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5", "Ward 6", "Ward 7", "Ward 8", "Ward 9", "Ward 10", "Ward 11", "Ward 12", "Ward 13", "Ward 14", "Ward 15"]},
    {"name": "District 11", "wards": ["Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5", "Ward 6", "Ward 7", "Ward 8", "Ward 9", "Ward 10", "Ward 11", "Ward 12", "Ward 13", "Ward 14", "Ward 15", "Ward 16"]},
    {"name": "District 12", "wards": ["An Phu Dong", "Dong Hung Thuan", "Hiep Thanh", "Tan Chanh Hiep", "Tan Hung Thuan", "Tan Thoi Hiep", "Tan Thoi Nhat", "Thanh Loc", "Thanh Xuan", "Thoi An", "Trung My Tay"]},
    {"name": "Binh Chanh", "wards": ["An Phu Tay", "Binh Chanh", "Binh Hung", "Binh Loi", "Da Phuoc", "Hung Long", "Le Minh Xuan", "Phạm Van Hai", "Quy Duc", "Tan Kien", "Tan Nhut", "Tan Quý Tay", "Tan Tuc", "Vinh Loc A", "Vinh Loc B"]},
    {"name": "Binh Tan", "wards": ["An Lac", "An Lac A", "Binh Hung Hoa", "Binh Hung Hoa A", "Binh Hung Hoa B", "Binh Tri Dong", "Binh Tri Dong A", "Binh Tri Dong B", "Tan Tao", "Tan Tao A"]},
    {"name": "Binh Thanh", "wards": ["Ward 1", "Ward 2", "Ward 3", "Ward 5", "Ward 6", "Ward 7", "Ward 11", "Ward 12", "Ward 13", "Ward 14", "Ward 15", "Ward 17", "Ward 19", "Ward 21", "Ward 22", "Ward 24", "Ward 25", "Ward 26", "Ward 27", "Ward 28"]},
    {"name": "Cu Chi", "wards": ["An Nhon Tay", "An Phu", "Binh My", "Cu Chi", "Hoa Phu", "Nhuan Duc", "Pham Van Coi", "Phu Hoa Dong", "Phu My Hung", "Tan An Hoi", "Tan Phu Trung", "Tan Thanh Dong", "Tan Thanh Tay", "Tan Thong Hoi", "Thai My", "Trung An", "Trung Lap Ha", "Trung Lap Thuong"]},
    {"name": "Go Vap", "wards": ["Ward 1", "Ward 3", "Ward 4", "Ward 5", "Ward 6", "Ward 7", "Ward 8", "Ward 9", "Ward 10", "Ward 11", "Ward 12", "Ward 13", "Ward 14", "Ward 15", "Ward 16", "Ward 17"]},
    {"name": "Hoc Mon", "wards": ["Ba Diem", "Dong Thanh", "Hoc Mon", "Nhi Binh", "Tan Hiep", "Tan Thoi Nhi", "Tan Xuan", "Thoi Tam Thon", "Trung Chanh", "Xuan Thoi Dong", "Xuan Thoi Son", "Xuan Thoi Thuong"]},
    {"name": "Nha Be", "wards": ["Hiep Phuoc", "Long Thoi", "Nhon Duc", "Phu Xuan", "Phuoc Kien", "Phuoc Loc"]},
    {"name": "Phu Nhuan", "wards": ["Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5", "Ward 7", "Ward 8", "Ward 9", "Ward 10", "Ward 11", "Ward 13", "Ward 14", "Ward 15", "Ward 17"]},
    {"name": "Tan Binh", "wards": ["Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5", "Ward 6", "Ward 7", "Ward 8", "Ward 9", "Ward 10", "Ward 11", "Ward 12", "Ward 13", "Ward 14", "Ward 15"]},
    {"name": "Tan Phu", "wards": ["Hiep Tan", "Hoa Thanh", "Phu Thanh", "Phu Tho Hoa", "Phu Trung", "Son Ky", "Tan Quy", "Tan Thanh", "Tan Thoi Hoa", "Tan Thoi Nhat", "Tay Thanh"]},
    {"name": "Thu Duc City", "wards": ["An Khanh", "An Loi Dong", "An Phu", "Binh Chieu", "Binh Tho", "Cat Lai", "Hiep Binh Chanh", "Hiep Binh Phuoc", "Hiep Phu", "Linh Chieu", "Linh Dong", "Linh Tay", "Linh Trung", "Linh Xuan", "Long Binh", "Long Phuoc", "Long Thanh My", "Long Truong", "Phu Huu", "Phuoc Binh", "Phuoc Long A", "Phuoc Long B", "Tam Binh", "Tam Phu", "Tang Nhon Phu A", "Tang Nhon Phu B", "Thanh My Loi", "Thao Dien", "Thu Thiem", "Truong Thanh", "Truong Tho"]}
]

# Street names for random address generation
street_names = [
    "Nguyen Hue", "Le Loi", "Dong Khoi", "Hai Ba Trung", "Nguyen Du", "Pasteur", "Nam Ky Khoi Nghia",
    "Le Duan", "Truong Dinh", "Vo Van Tan", "Ly Tu Trong", "Nguyen Thai Hoc", "Tran Hung Dao",
    "Cach Mang Thang Tam", "Dien Bien Phu", "Vo Thi Sau", "Le Van Sy", "Nguyen Van Cu", "Ton Duc Thang",
    "Hoang Van Thu", "Phan Xich Long", "Nguyen Trai", "Au Co", "Lac Long Quan", "Xo Viet Nghe Tinh",
    "Kha Van Can", "Phan Van Tri", "Quang Trung", "Le Van Viet", "Vo Van Ngan", "Duong Ba Trac",
    "Huynh Tan Phat", "Nguyen Thi Thap", "Pham The Hien", "Ha Huy Giap", "Le Van Khuong", "To Ky",
    "Nguyen Anh Thu", "Truong Chinh", "Le Duc Tho", "Phan Huy Ich"
]

# Building types for station names
building_types = [
    "Market", "Park", "Plaza", "Station", "Square", "Center", "Mall", "Hub", "Tower", "Complex",
    "Corner", "Junction", "Gateway", "Terminal", "Exchange", "Avenue", "Campus", "Depot", "Port", "Point"
]

# Name prefixes for station names
name_prefixes = [
    "Central", "City", "Metro", "Urban", "Golden", "Silver", "Green", "Blue", "Red", "Yellow",
    "Royal", "Imperial", "Modern", "New", "Grand", "Main", "Prime", "Elite", "People's", "Saigon",
    "Eastern", "Western", "Northern", "Southern", "Downtown", "Uptown", "Riverside", "Lakeside", "Hillside", "Parkside"
]

# Function to generate a random station
def generate_random_station():
    # Select random district and ward
    district_data = random.choice(hcmc_districts)
    district = district_data["name"]
    ward = random.choice(district_data["wards"])

    # Generate random coordinates within Ho Chi Minh City boundaries
    # HCMC rough coordinates: 10.7-10.9 latitude, 106.6-106.8 longitude
    latitude = random.uniform(10.7, 10.9)
    longitude = random.uniform(106.6, 106.8)

    # Generate address
    street_name = random.choice(street_names)
    street_number = random.randint(1, 999)
    address = f"{street_number} {street_name} Street"

    # Generate station name
    prefix = random.choice(name_prefixes)
    building = random.choice(building_types)
    name = f"{prefix} {ward} {building}"

    # Generate random capacity between 10 and 50
    capacity = random.randint(10, 50)

    # Create station data
    station_data = {
        "name": name,
        "address": address,
        "latitude": latitude,
        "longitude": longitude,
        "city": "Ho Chi Minh City" if district != "Thu Duc City" else "Thu Duc City",
        "district": district,
        "ward": ward,
        "base64Image": "",
        "capacity": capacity
    }

    return station_data

# Function to post station data to API
def post_station(station_data):
    try:
        response = requests.post(API_URL, headers=HEADERS, json=station_data)
        if response.status_code == 201 or response.status_code == 200:
            print(f"Successfully created station: {station_data['name']}")
            return True
        else:
            print(f"Failed to create station. Status code: {response.status_code}")
            print(f"Response: {response.text}")
            return False
    except Exception as e:
        print(f"Error creating station: {str(e)}")
        return False

def main():
    success_count = 0
    failed_count = 0

    # Create 1000 stations
    for i in range(1000):
        station_data = generate_random_station()

        print(f"Creating station {i+1}/1000: {station_data['name']}")

        # Post station data to API
        if post_station(station_data):
            success_count += 1
        else:
            failed_count += 1

        # Add a small delay to avoid overwhelming the API
        time.sleep(0.5)

        # Optional: Print progress every 50 stations
        if (i + 1) % 50 == 0:
            print(f"Progress: {i+1}/1000 stations processed. Success: {success_count}, Failed: {failed_count}")

    print(f"Completed! Total stations created: {success_count}. Failed: {failed_count}")

if __name__ == "__main__":
    main()