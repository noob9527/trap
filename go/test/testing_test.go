package test

import (
	"fmt"
	"testing"
)

func TestErrorf(t *testing.T) {
	t.Error()
	fmt.Println("asdf")
}

func TestFatalF(t *testing.T) {
	t.Fatal()
	fmt.Println("qwer")
}
