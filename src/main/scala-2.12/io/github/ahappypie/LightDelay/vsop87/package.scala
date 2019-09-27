package io.github.ahappypie.LightDelay

/**
 * Adapted from libnova - https://sourceforge.net/projects/libnova/
 * Commit hash 655bcd
 * Last Updated 2019-09-26
 * Files are organized as a workaround for the JVM's 64KB method limit
 * Some datasets can fit all in one file, some datasets must be separated out into private objects
 * However, the public interface remains the same:
 * $PLANET.${lat | long | rad}.$DATASET
 * Every dataset is an array of tuples, namely, Array[(Double, Double, Double)]
 *
 * Helpful regex used to do some formatting: \[LONG_L\d] = \(
 */

package object vsop87 {

}

/**
 * sbt note - this was originally a package-info.java file that bumped into https://github.com/sbt/sbt/issues/4665
 * It caused ArrayIndexOutOfBounds and was solved immediately by turning it into a Scala package object
 */