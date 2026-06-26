from flask import Flask
from flask_jwt_extended import JWTManager
from pymongo import MongoClient
from .config import Config

jwt = JWTManager()
mongo_client: MongoClient = None
db = None


def create_app():
    app = Flask(__name__)
    app.config.from_object(Config)

    jwt.init_app(app)

    global mongo_client, db
    mongo_client = MongoClient(app.config["MONGO_URI"])
    db = mongo_client.get_default_database()

    from .routes.auth import auth_bp
    from .routes.history import history_bp
    from .routes.groups import groups_bp
    app.register_blueprint(auth_bp, url_prefix="/auth")
    app.register_blueprint(history_bp, url_prefix="/history")
    app.register_blueprint(groups_bp, url_prefix="/groups")

    return app
