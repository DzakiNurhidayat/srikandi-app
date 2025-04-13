package org.example.project.domain.services.interfaces

import org.example.project.model.entities.FormSatu
import org.example.project.model.request.FormSatuRequest

interface IFormSatuService : IEntityService<FormSatuRequest, Pair<Int, Int>, FormSatu>
