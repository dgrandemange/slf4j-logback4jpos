slf4j-logback4jpos
==================

A lf4j-logback appender implementation for jPos.

This implementation work is inspired by Victor Salaman's initial work on <a href="https://github.com/jpos/jPOS-EE">jPOS-EE</a> core module, which is the reference implementation. 

This variation deals with jPos Loggeable objects in a way so that their formatting by logback is reversed, and delegated to the underlying jPos logger.
Support for logback Encoder is also added.
