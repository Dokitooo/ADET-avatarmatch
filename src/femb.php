<?php
/*
-------------------------------------------------------------
-------------------------------------------------------------

BRUCELO, Alyssa Mae B.
EBUENGA, Kristel Ann B.
FENIS, Austin B.
MARFIL, John Marvin G.
BUCS BSIT 3C AY 2025-2026
IT 106 - Application Development and Emerging Technologies

-------------------------------------------------------------
-------------------------------------------------------------

SERVER-SIDE API
Contributor: MARFIL, John Marvin G.

-------------------------------------------------------------
-------------------------------------------------------------
*/

header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE");
header("Access-Control-Allow-Headers: Content-Type");

$input = file_get_contents("php://input");
$request = json_decode($input, true);
if (!$request) {
      $request = $_POST;
}

$username = $request['username'] ?? null;
$word = $request['word'] ?? null;

// 1. Get the Request Method
$method = $_SERVER['REQUEST_METHOD'];

// 2. Parse Input (Handle JSON, POST, and GET)
$input = file_get_contents("php://input");
$request = json_decode($input, true) ?? [];

// Unified parameter retrieval to support all client types
$username = $request['username'] ?? $_POST['username'] ?? $_GET['username'] ?? null;
$word = $request['word'] ?? $_POST['word'] ?? $_GET['word'] ?? null;

// 3. Validation Logic based on Method
if (!$username) {
      echo json_encode([
            "status" => "error",
            "message" => "username is required"
      ]);
      exit;
}

// Word is only strictly required for creating (POST) or updating (PUT)
if (($method === 'POST' || $method === 'PUT') && !$word) {
      echo json_encode([
            "status" => "error",
            "message" => "word is required"
      ]);
      exit;
}

// 4. Validate Formats
if (!checkUsername($username)) {
      echo json_encode(["status" => "error", "message" => "Invalid username format"]);
      exit;
}

if ($word && strlen($word) > 15) {
      echo json_encode(["status" => "error", "message" => "word must be 15 characters or less"]);
      exit;
}

// 5. Database Connection (Fixing the error label here too)
$conn = new mysqli("localhost", "root", "", "IT3Cadet_femb");

if ($conn->connect_error) {
      echo json_encode([
            "status" => "error",
            "message" => "Database connection failed: " . $conn->connect_error
      ]);
      exit;
}

// CHECKS if the username has valid length and characters.
function checkUsername($username)
{
      if (strlen($username) > 15) {
            return false;
      } else {
            return preg_match('/^[a-zA-Z0-9_.]{1,15}$/', $username);
      }
}

// CHECKS if the username already exists in the database.
function findUsername($conn, $anchorUsername)
{
      $select = $conn->prepare("SELECT username FROM avatar WHERE username = ?");
      $select->bind_param("s", $anchorUsername);
      $select->execute();
      $result = $select->get_result();
      $select->close();

      if ($row = $result->fetch_assoc()) {
            return true;
      } else {
            return false;
      }
}

// CHECKS if main avatar has a match and updates all other avatars it matches with the same word.
function findMatch($conn, $wordMatch, $anchorUsername)
{
      // FINDS all other avatars that currently has the same words.
      $select = $conn->prepare("SELECT username, matches FROM avatar WHERE word = ?");
      $select->bind_param("s", $wordMatch);
      $select->execute();
      $result = $select->get_result();

      // UPDATES the 'matches' and 'rank' of each avatar found with the same word.
      $count = 0;
      $update = $conn->prepare("UPDATE avatar SET matches = ?, rank = ? WHERE username = ?");
      while ($row = $result->fetch_assoc()) {
            $username = $row['username'];

            // ENSURES that the main avatar is excluded.
            if ($username != $anchorUsername) {
                  $matches = $row['matches'] + 1;
                  $rank = matchRank($matches);

                  $update->bind_param("iss", $matches, $rank, $username);
                  $update->execute();

                  $count++;
            }
      }
      $update->close();

      // RETURNS the number of matches found with the same word, excluding the main avatar.
      return $count;
}

// DETERMINES the rank of the avatar based on the number of matches.
function matchRank($matchNum)
{
      if ($matchNum < 10) {
            return "novice";
      } else if ($matchNum < 100) {
            return "apprentice";
      } else if ($matchNum < 1000) {
            return "master";
      } else {
            return "legend";
      }
}

$method = $_SERVER['REQUEST_METHOD'];

switch ($method) {
      case 'GET':
            if (findUsername($conn, $username)) {
                  $select = $conn->prepare("SELECT * FROM avatar WHERE username = ?");
                  $select->bind_param("s", $username);
                  $select->execute();
                  $result = $select->get_result();
                  $row = $result->fetch_assoc();
                  $select->close();

                  echo json_encode([
                        "status" => "success",
                        "username" => $row['username'],
                        "word" => $row['word'],
                        "matches" => $row['matches'],
                        "rank" => $row['rank']
                  ]);
            } else {
                  echo json_encode([
                        "status" => "error",
                        "message" => "username not found"
                  ]);
                  exit;
            }

            break;
      case 'POST':
            if (!findUsername($conn, $username)) {
                  // IF the username does not exist, it will be created with a default word and initial matches of 0.
                  $word = $request['word'] ?? 'apple';
                  $matches = 0;
                  $rank = matchRank($matches);

                  $insert = $conn->prepare("INSERT INTO avatar (username, word, matches, rank) VALUES (?, ?, ?, ?)");
                  $insert->bind_param("ssis", $username, $word, $matches, $rank);
                  $insert->execute();
                  $insert->close();

                  echo json_encode([
                        "status" => "success",
                        "username" => $username,
                        "word" => $word,
                        "matches" => $matches,
                        "rank" => $rank
                  ]);
            } else {
                  echo json_encode([
                        "status" => "error",
                        "message" => "username already exists"
                  ]);
                  exit;
            }

            break;
      case 'PUT':
            if (findUsername($conn, $username)) {
                  // ADDS the new matches to the existing matches and updates the rank accordingly.
                  $matches = $request['matches'] + findMatch($conn, $word, $username);
                  $rank = matchRank($matches);

                  $update = $conn->prepare("UPDATE avatar SET matches = ?, rank = ? WHERE username = ?");
                  $update->bind_param("iss", $matches, $rank, $username);
                  $update->execute();
                  $update->close();

                  echo json_encode([
                        "status" => "success",
                        "username" => $username,
                        "word" => $word,
                        "matches" => $matches,
                        "rank" => $rank
                  ]);
            } else {
                  echo json_encode([
                        "status" => "error",
                        "message" => "username not found"
                  ]);
                  exit;
            }

            break;
      case 'DELETE':
            if (findUsername($conn, $username)) {
                  $delete = $conn->prepare("DELETE FROM avatar WHERE username = ?");
                  $delete->bind_param("s", $username);
                  $delete->execute();
                  $delete->close();

                  echo json_encode([
                        "status" => "success",
                        "message" => "user deleted",
                        "username" => $username
                  ]);
            } else {
                  echo json_encode([
                        "status" => "error",
                        "message" => "username not found"
                  ]);
            }

            break;
      default:
            http_response_code(405);
            echo json_encode([
                  "status" => "error",
                  "message" => "Method not allowed"
            ]);
            break;
}

$conn->close();
?>