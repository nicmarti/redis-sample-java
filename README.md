redis-sample-java
=================

English:
========

This is a set of very simple test cases to demonstrate Redis with Java.
I wrote a simple class with JUnit+Fest Assert for Devoxx France conference.

Pre-requisites : A Redis server, up and running on port 6379. Check Redis quickstart documentation here
http://redis.io/topics/quickstart

The test executes itself on DB 2 (with a SELECT 2) thus it should not impact a local existing DB.


En Français dans le texte :
===========================

Ce projet est un petit exemple de code simple, écrit pour la conférence Devoxx France.

Elle utilise le driver Jedis, le plus utilisé dès lors qu'il s'agit de faire du Java avec une base Redis.

Pour tester, vous devez d'abord installer un serveur Redis sur votre machine (http://redis.io/topics/quickstart)
Par défaut, Redis écoute sur le port TCP 6379.

Le test s'execute sur l'instance 2 de Redis en effectue un flushdb (vide toute la base 2).
Si vous ne souhaitez pas installer un serveur Redis, vous pouvez utiliser "Redis to Go" qui offre des plans gratuits
pour une petite base de 5Mo sans sauvegarde (https://redistogo.com).

Nicolas Martignole
@nmartignole
