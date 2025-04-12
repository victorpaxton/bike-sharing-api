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

# Binh Duong districts and wards
binh_duong_districts = [
    {"name": "Thu Dau Mot", "wards": ["Chanh My", "Chanh Nghia", "Dinh Hoa", "Hiep An", "Hiep Thanh", "Phu Cuong", "Phu Hoa", "Phu Loi", "Phu My", "Phu Tan", "Phu Tho", "Tan An"]},
    {"name": "Thuan An", "wards": ["An Phu", "An Son", "An Thanh", "Binh Chuan", "Binh Hoa", "Binh Nham", "Hung Dinh", "Lai Thieu", "Thuan Giao", "Vinh Phu"]},
    {"name": "Di An", "wards": ["An Binh", "Binh An", "Binh Thang", "Di An", "Dong Hoa", "Tan Binh", "Tan Dong Hiep"]},
    {"name": "Ben Cat", "wards": ["An Dien", "An Tay", "Chanh Phu Hoa", "Hoa Loi", "My Phuoc", "Phu An", "Tan Dinh", "Thoi Hoa"]},
    {"name": "Tan Uyen", "wards": ["Binh My", "Huu Dinh", "Khanh Binh", "Phu Chanh", "Tan Hiep", "Tan Phuoc Khanh", "Tan Vinh Hiep", "Thanh Phuoc", "Thuan Giao", "Uyen Hung", "Vinh Tan"]},
    {"name": "Bac Tan Uyen", "wards": ["Binh My", "Dat Cuoc", "Hieu Liem", "Tan Binh", "Tan Dinh", "Tan Lap", "Tan My", "Thuong Tan", "Tru Van Tho"]},
    {"name": "Bau Bang", "wards": ["Cay Truong II", "Hung Hoa", "Lai Hung", "Lai Uyen", "Long Nguyen", "Tan Hung", "Tru Van Tho"]},
    {"name": "Phu Giao", "wards": ["An Binh", "An Linh", "An Long", "An Thai", "Phuoc Hoa", "Phuoc Sang", "Tam Lap", "Tan Hiep", "Tan Long", "Vinh Hoa"]},
    {"name": "Dau Tieng", "wards": ["An Lap", "Dinh An", "Dinh Hiep", "Dinh Thanh", "Long Hoa", "Long Tan", "Minh Hoa", "Minh Thanh", "Minh Tan", "Thanh An", "Thanh Tuyen"]}
]

# Vung Tau city wards
vung_tau_wards = ["Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5", "Ward 7", "Ward 8", "Ward 9", "Ward 10", "Ward 11", "Ward 12", "Thang Nhat", "Rach Dua", "Nguyen An Ninh", "Long Son", "Long Huong"]

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

# Binh Duong street names
binh_duong_street_names = [
    "Binh Duong Avenue", "Thuan An", "My Phuoc Tan Van", "DT743", "DT746", "National Highway 13",
    "30/4", "Tran Hung Dao", "Le Hong Phong", "Ly Thuong Kiet", "Phu Loi", "Cach Mang Thang Tam",
    "Phan Dinh Phung", "Huynh Van Luy", "Le Thi Trung", "Ngo Quyen", "Phan Boi Chau", "Nguyen Dinh Chieu",
    "Binh Thung", "Doc Lap", "Tran Van On", "Nguyen Thi Minh Khai", "Tran Quoc Toan", "Nguyen An Ninh",
    "Yersin", "Pham Ngoc Thach", "Hung Vuong", "Pham Van Dong", "Chau Van Tiep", "Vo Van Kiet"
]

# Vung Tau street names
vung_tau_street_names = [
    "Ba Cu", "Hoang Hoa Tham", "Le Hong Phong", "Le Loi", "Ly Thuong Kiet", "Nam Ky Khoi Nghia",
    "Nguyen An Ninh", "Nguyen Hue", "Nguyen Trai", "Phan Chu Trinh", "Quang Trung", "Thong Nhat",
    "Tran Hung Dao", "Tran Phu", "Truong Cong Dinh", "Vo Thi Sau", "Hoang Dieu", "Nguyen Thai Hoc",
    "Le Quy Don", "Nguyen Van Troi", "Phan Boi Chau", "Xo Viet Nghe Tinh", "Nguyen Thien Thuat",
    "Ha Long", "Binh Gia", "Tran Phu"
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

# Binh Duong specific prefixes
binh_duong_prefixes = [
    "Binh Duong", "Industrial", "Becamex", "VSIP", "Manufacturing", "Commerce", "Tech", "Innovation",
    "Ceramic", "Furniture", "Textile", "Export", "Business", "Enterprise", "Production", "Factory"
]

# Vung Tau specific prefixes
vung_tau_prefixes = [
    "Coastal", "Beach", "Ocean", "Sea", "Harbor", "Bay", "Port", "Maritime", "Resort", "Tourism",
    "Sunset", "Paradise", "View", "Petroleum", "Offshore", "Gulf", "Breeze", "Sunshine", "Seaside", "Wave"
]

# Function to generate a random HCMC station
def generate_hcmc_station():
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

# Function to generate a random Binh Duong station
def generate_binh_duong_station():
    # Select random district and ward
    district_data = random.choice(binh_duong_districts)
    district = district_data["name"]
    ward = random.choice(district_data["wards"])

    # Generate random coordinates within Binh Duong boundaries
    # Binh Duong rough coordinates: 10.9-11.2 latitude, 106.5-106.8 longitude
    latitude = random.uniform(10.9, 11.2)
    longitude = random.uniform(106.5, 106.8)

    # Generate address
    street_name = random.choice(binh_duong_street_names)
    street_number = random.randint(1, 999)
    address = f"{street_number} {street_name}"

    # Generate station name
    prefix = random.choice(binh_duong_prefixes + name_prefixes)
    building = random.choice(building_types)
    name = f"{prefix} {ward} {building}"

    # Generate random capacity between 10 and 40
    capacity = random.randint(10, 40)

    # Create station data
    station_data = {
        "name": name,
        "address": address,
        "latitude": latitude,
        "longitude": longitude,
        "city": "Binh Duong Province",
        "district": district,
        "ward": ward,
        "base64Image": "",
        "capacity": capacity
    }

    return station_data

# Function to generate a random Vung Tau station
def generate_vung_tau_station():
    # Select random ward
    ward = random.choice(vung_tau_wards)

    # Generate random coordinates within Vung Tau boundaries
    # Vung Tau rough coordinates: 10.3-10.4 latitude, 107.05-107.15 longitude
    latitude = random.uniform(10.3, 10.4)
    longitude = random.uniform(107.05, 107.15)

    # Generate address
    street_name = random.choice(vung_tau_street_names)
    street_number = random.randint(1, 999)
    address = f"{street_number} {street_name} Street"

    # Generate station name
    prefix = random.choice(vung_tau_prefixes + name_prefixes)
    building = random.choice(building_types)
    name = f"{prefix} {ward} {building}"

    # Generate random capacity between 15 and 50 (tourist area might need more bikes)
    capacity = random.randint(15, 50)

    # Create station data
    station_data = {
        "name": name,
        "address": address,
        "latitude": latitude,
        "longitude": longitude,
        "city": "Vung Tau City",
        "district": "Vung Tau",
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

    # Create 150 Binh Duong stations
    print("\n=== Creating Binh Duong Province stations ===")
    binh_duong_success = 0
    for i in range(150):
        station_data = generate_binh_duong_station()

        print(f"Creating Binh Duong station {i+1}/150: {station_data['name']}")

        # Post station data to API
        if post_station(station_data):
            success_count += 1
            binh_duong_success += 1
        else:
            failed_count += 1

        # Add a small delay to avoid overwhelming the API
        time.sleep(0.3)

        # Optional: Print progress every 25 stations
        if (i + 1) % 25 == 0:
            print(f"Binh Duong Progress: {i+1}/150 stations processed. Success: {binh_duong_success}")

    # Create 50 Vung Tau stations
    print("\n=== Creating Vung Tau City stations ===")
    vung_tau_success = 0
    for i in range(50):
        station_data = generate_vung_tau_station()

        print(f"Creating Vung Tau station {i+1}/50: {station_data['name']}")

        # Post station data to API
        if post_station(station_data):
            success_count += 1
            vung_tau_success += 1
        else:
            failed_count += 1

        # Add a small delay to avoid overwhelming the API
        time.sleep(0.3)

        # Optional: Print progress every 10 stations
        if (i + 1) % 10 == 0:
            print(f"Vung Tau Progress: {i+1}/50 stations processed. Success: {vung_tau_success}")

    print(f"\nCompleted! Total stations created: {success_count}. Failed: {failed_count}")
    print(f"HCMC: ~{success_count - binh_duong_success - vung_tau_success} stations")
    print(f"Binh Duong: {binh_duong_success} stations")
    print(f"Vung Tau: {vung_tau_success} stations")

if __name__ == "__main__":
    main()