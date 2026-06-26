from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from datetime import datetime, timezone
import random
import string
from app import db

groups_bp = Blueprint("groups", __name__)


def _generate_code(length=6):
    chars = string.ascii_uppercase + string.digits
    while True:
        code = "".join(random.choices(chars, k=length))
        if not db.groups.find_one({"code": code}):
            return code


@groups_bp.post("/create")
@jwt_required()
def create_group():
    user_id = get_jwt_identity()
    code = _generate_code()
    db.groups.insert_one({
        "code": code,
        "members": [user_id],
        "created_at": datetime.now(timezone.utc),
    })
    return jsonify(code=code), 201


@groups_bp.post("/join")
@jwt_required()
def join_group():
    user_id = get_jwt_identity()
    data = request.get_json(silent=True) or {}
    code = data.get("code", "").strip().upper()

    group = db.groups.find_one({"code": code})
    if not group:
        return jsonify(error="codice gruppo non valido"), 404

    if user_id not in group["members"]:
        db.groups.update_one({"code": code}, {"$push": {"members": user_id}})

    return jsonify(code=code), 200
