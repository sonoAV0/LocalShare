from flask import Blueprint, request, jsonify
from flask_jwt_extended import create_access_token, jwt_required, get_jwt_identity
from datetime import datetime, timezone
import uuid
from app import db

auth_bp = Blueprint("auth", __name__)


@auth_bp.post("/register")
def register():
    user_id = str(uuid.uuid4())
    db.users.insert_one({
        "user_id": user_id,
        "created_at": datetime.now(timezone.utc),
    })
    return jsonify(user_id=user_id), 201


@auth_bp.post("/login")
def login():
    data = request.get_json(silent=True) or {}
    user_id = data.get("user_id", "").strip()

    if not user_id or not db.users.find_one({"user_id": user_id}):
        return jsonify(error="user_id non valido"), 401

    token = create_access_token(identity=user_id)
    return jsonify(access_token=token), 200


@auth_bp.get("/me")
@jwt_required()
def me():
    user_id = get_jwt_identity()
    return jsonify(user_id=user_id), 200
