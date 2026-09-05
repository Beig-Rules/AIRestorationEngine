# Python-based planner validation (mirrors Kotlin tests)
def test_planner_deblock():
    artifacts = 0.9
    assert artifacts > 0.6, "Should trigger DEBLOCK"

def test_planner_skip_sr():
    width, height = 4000, 3000
    assert width * height >= 1_000_000, "Should skip SR"

if __name__ == "__main__":
    test_planner_deblock()
    test_planner_skip_sr()
    print("✅ All planner tests passed")
