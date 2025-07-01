.PHONY: lint format check

# 코드 린팅
lint:
	ruff check .
	pyright .

# 코드 포매팅
format:
	ruff format .

# 타입 체킹
typecheck:

# 모든 검사 실행
check: lint typecheck
	@echo "All checks completed!"
