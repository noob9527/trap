package basic

import (
	"testing"
)

func TestNil(t *testing.T) {
	defer func() {
		if r := recover(); r == nil {
			t.Errorf("The code did not panic")
		}
	}()

	var s *string

	_ = *s
}
