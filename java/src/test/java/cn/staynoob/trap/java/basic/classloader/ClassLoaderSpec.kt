package cn.staynoob.trap.java.basic.classloader

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URLClassLoader

class ClassLoaderSpec {

    private val url = this::class.java.classLoader.getResource("fixture/classloader")!!
    private val urls = listOf(
            // add suffix "/"
            File(url.toURI()).toURI().toURL()
    )

    @Test
    @DisplayName("url classloader basic usage")
    fun test100() {
        val classloader = URLClassLoader(urls.toTypedArray())
        val clazz = classloader.loadClass("cn.staynoob.trap.java.basic.classloader.ClassloaderFixture")
        val obj = clazz.newInstance()
        assertThat(obj).isInstanceOf(IClassloaderFixture::class.java)
    }

    /**
     * >
     * <p> The <tt>ClassLoader</tt> class uses a delegation model to search for
     * classes and resources.  Each instance of <tt>ClassLoader</tt> has an
     * associated parent class loader.  When requested to find a class or
     * resource, a <tt>ClassLoader</tt> instance will delegate the search for the
     * class or resource to its parent class loader before attempting to find the
     * class or resource itself.  The virtual machine's built-in class loader,
     * called the "bootstrap class loader", does not itself have a parent but may
     * serve as the parent of a <tt>ClassLoader</tt> instance.
     * @see java.lang.ClassLoader
     */
    @Test
    @DisplayName("parent classloader is usually needed to load a class (because you need to load its parent class usually)")
    fun test200() {
        val classloader = URLClassLoader(urls.toTypedArray(), null)
        assertThatThrownBy {
            classloader.loadClass("cn.staynoob.trap.java.basic.classloader.ClassloaderFixture")
        }.isInstanceOf(NoClassDefFoundError::class.java)
    }
}
