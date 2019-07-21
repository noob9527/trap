package test

import (
	"testing"
)

//func TestErrorf(t *testing.T) {
//	t.Error()
//	fmt.Println("asdf")
//}
//
//func TestFatalF(t *testing.T) {
//	t.Fatal()
//	fmt.Println("qwer")
//}

func TestPanic(t *testing.T) {
	defer func() {
		if r := recover(); r == nil {
			t.Errorf("The code did not panic")
		}
	}()

	// The following is the code under test
	panic("gotcha")
}
