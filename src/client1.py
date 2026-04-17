# Client Application No. 1 - Avatar Word Match API Client Using Python
# Contributor: Kristel Ann B. Ebuenga


import requests
import json

# API endpoint
API_URL = "http://localhost/femb.php"

def display_menu():
    print("\n" + "="*50)
    print("AVATAR WORD MATCH API CLIENT")
    print("="*50)
    print("1. GET - View avatar")
    print("2. POST - Create new avatar")
    print("3. PUT - Update word & find matches")
    print("4. DELETE - Remove avatar")
    print("5. EXIT")
    print("-"*50)

def get_avatar():
    username = input("Enter username: ")
    response = requests.get(API_URL, params={"username": username})
    
    if response.status_code == 200:
        data = response.json()
        if data.get("status") == "success":
            print(f"\nAvatar Found!")
            print(f"   Username: {data['username']}")
            print(f"   Word: {data['word']}")
            print(f"   Matches: {data['matches']}")
            print(f"   Rank: {data['rank']}")
        else:
            print(f"\nError: {data.get('message', 'Unknown error')}")

    else:
        print(f"\nHTTP Error: {response.status_code}")
        
def create_avatar():
    username = input("Enter new username (max 15 chars, letters/numbers/_.): ")
    word = input("Enter word (max 15 chars, default='apple'): ") or "apple"
    
    data = {"username": username, "word": word}
    response = requests.post(API_URL, data=data)
    
    if response.status_code == 200:
        result = response.json()
        if result.get("status") == "success":
            print(f"\nAvatar Created!")
            print(f"   Username: {result['username']}")
            print(f"   Word: {result['word']}")
            print(f"   Matches: {result['matches']}")
            print(f"   Rank: {result['rank']}")
        else:
            print(f"\nError: {result.get('message')}")
    else:
        print(f"\nHTTP Error: {response.status_code}")

def update_avatar():
    username = input("Enter username: ")
    word = input("Enter NEW word (max 15 chars): ")
    
    # PUT request sends JSON body
    data = {"username": username, "word": word, "matches": 0}
    response = requests.put(API_URL, json=data)
    
    if response.status_code == 200:
        result = response.json()
        if result.get("status") == "success":
            print(f"\nAvatar Updated!")
            print(f"   Username: {result['username']}")
            print(f"   Word: {result['word']}")
            print(f"   Total Matches: {result['matches']}")
            print(f"   New Rank: {result['rank']}")
        else:
            print(f"\nError: {result.get('message')}")
    else:
        print(f"\nHTTP Error: {response.status_code}")

def delete_avatar():
    username = input("Enter username to delete: ")
    response = requests.delete(API_URL, params={"username": username})
    
    if response.status_code == 200:
        result = response.json()
        if result.get("status") == "success":
            print(f"\n{result.get('message', 'User deleted')}")
            print(f"   Username: {result['username']}")
        else:
            print(f"\nError: {result.get('message')}")
    else:
        print(f"\n HTTP Error: {response.status_code}")

# Main program
if __name__ == "__main__":
    print("\n AVATAR WORD MATCH GAME CLIENT ")
    print("Connecting to: " + API_URL)
    
    while True:
        display_menu()
        choice = input("Enter choice (1-5): ")
        
        if choice == "1":
            get_avatar()
        elif choice == "2":
            create_avatar()
        elif choice == "3":
            update_avatar()
        elif choice == "4":
            delete_avatar()
        elif choice == "5":
            print("\n Goodbye!")
            break
        else:
            print("\nInvalid choice. Try again.")
        
        input("\nPress Enter to continue...")