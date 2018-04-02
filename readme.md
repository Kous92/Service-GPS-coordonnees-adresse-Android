# Service GPS Android
Avec les notions que j'ai apprises en M1 dans le cadre du module Mobile Development en complément de tutoriels sur YouTube (Services, GPS, API), j'ai conçu une appli qui:
- Récupère les coordonnées GPS (longitude et latitude) via la localisation et une connexion Internet (classes LocationManager, LocationListener et Location).
- Synchronise en temps réel la position GPS via le service quand il est activé (il est actif même quand l'application est inactive).
- Traduit à l'aide d'une API les coordonnées GPS en addresse physique à l'aide d'une structure au format JSON (classe Adress).
- Pour ce qui est du format JSON: les classes RequestQueue, JsonObjectRequest (éventuellement JsonArrayRequest) sont utilisées, de la librairie du module Volley.

# API Utilisée:
Google Maps Geocoding API: https://developers.google.com/maps/documentation/geocoding/intro?hl=fr

# Module Volley
Plus d'informations ici: https://developer.android.com/training/volley/index.html

# Fonctionnement de l'application
- À la première utilisation: autoriser la localisation
- Appuyer sur le bouton "Démarrer le service" pour lancer la géolocalisation et la synchronisation en temps réel (coordonnées + adresse).
- Si le service est lancé, appuyer sur le bouton "Arrêter le service" pour stopper la synchronisation et la géolocalisation.

# Politique de confidentialité
Aucune information sur votre position est envoyée sur un quelconque serveur, je m'y engage là-dessus, de même pour Google avec son API.
