�����W�J�v���O���� Java��


[�f�B���N�g��/�t�@�C���̍\��]

CellMLonGPU			Eclipse�̃v���W�F�N�g
+-- .classpath			Eclipse���g�p����t�@�C��
+-- .project			Eclipse���g�p����t�@�C��
+-- .settings			Eclipse���g�p����f�B���N�g��
+-- bin				Java�N���X�t�@�C���i�[�f�B���N�g��
+-- gen_all.bat			�f�o�b�O�p���s�t�@�C��
+-- lib				Java jar�t�@�C���i�[�f�B���N�g��
+-- model			�f�o�b�O�p�f�[�^�t�@�C���i�[�f�B���N�g��
+-- out_common_gen		�f�o�b�O�p�����W�J���ʊi�[�f�B���N�g��(���ʃR�[�h)
+-- out_cuda_gen		�f�o�b�O�p�����W�J���ʊi�[�f�B���N�g��(cuda�R�[�h)
+-- out_java_bigdecimal_gen	�f�o�b�O�p�����W�J���ʊi�[�f�B���N�g��(java bigdecimal�R�[�h)
+-- out_java_gen		�f�o�b�O�p�����W�J���ʊi�[�f�B���N�g��(java�R�[�h)
+-- out_simple_gen		�f�o�b�O�p�����W�J���ʊi�[�f�B���N�g��(�V���v���R�[�h)
+-- parser.bat			�R�}���h�Ŏ��s�R�}���h�t�@�C��
+-- parserGUI.bat		GUI�Ŏ��s�T�u�R�}���h�t�@�C��
+-- readme.txt			���̃t�@�C��
+-- src				Java�\�[�X�t�@�C���i�[�f�B���N�g��
+-- startGUI.bat		GUI�Ŏ��s�R�}���h�t�@�C��


[���s���@]

��GUI��

startGUI.bat ���_�u���N���b�N���ĉ������B
GUI�E�B���h�E���\������܂��B
RelML�t�@�C������ݒ肵�A���s�{�^���������Ɛ����W�J���܂��B


���R�}���h��

parser.bat �����s�R�}���h�ł��B
�����Ȃ��ŋN������ƁA�g�p���@���o�͂��܂��B

�R�}���h�̐���
  parser [ -g name ] RelML [output-option]

  -g: �����R�[�h�w��I�v�V����
      �w�肵�Ȃ���΁Acuda �R�[�h�𐶐����܂��B

  name:
    cuda	cuda �R�[�h
    common	���ʃR�[�h
    simple	�V���v���R�[�h
    java	java�R�[�h
    java_bigdecimal	java bigdecimal�R�[�h

  RelML: RelML�t�@�C�����w�肵�܂��B

  output-option: �o�͐���w�肷��I�v�V����
    ���L�̏��Ɏw�肵�܂��B
    �o�̓f�B���N�g�� [�ϐ��֌W�t�@�C�� [���������t�@�C�� [�v���O�����t�@�C��]]]

    ���ׂĎw�肷��K�v�͂���܂���B
    �w�肪�Ȃ��ꍇ�ɂ͉��L�̎w��Ƃ݂Ȃ��܂��B
      �o�̓f�B���N�g��: �R�}���h������f�B���N�g��
      �ϐ��֌W�t�@�C��: relation.txt
      ���������t�@�C��: initialize.txt
      �v���O�����t�@�C��: �W���o��

    �����R�[�h�w��I�v�V������ simple �̏ꍇ�A
    �v���O�����t�@�C���w��͖�������܂��B


�ȏ�

